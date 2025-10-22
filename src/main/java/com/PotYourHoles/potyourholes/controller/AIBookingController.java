package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.dto.AIDto;
import com.PotYourHoles.potyourholes.model.AImodel;
import com.PotYourHoles.potyourholes.model.Appointments;
import com.PotYourHoles.potyourholes.repository.AIRepository;
import com.PotYourHoles.potyourholes.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-gg85ccox0-botme2121-2892s-projects.vercel.app}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class AIBookingController {

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Inject AI service URL from environment variable or default to localhost
    @Value("${AI_SERVICE_URL:http://localhost:5002/predict}")
    private String aiServiceUrl;

    private final String UPLOADS_DIR = System.getProperty("user.dir") + "/uploads/";

    // ------------------- GET ALL APPOINTMENTS WITH AI SEVERITY -------------------
    @GetMapping("/appointments")
    public ResponseEntity<List<AIDto>> getAllAppointments() {
        List<Appointments> appointments = appointmentRepository.findAll();
        RestTemplate restTemplate = new RestTemplate();

        List<AIDto> response = appointments.stream().map(appt -> {
            String severity = appt.getSeverity();
            String imageFileName = appt.getPotholePhoto();

            // Only call AI server if severity is missing or "pending"
            if ((severity == null || severity.equalsIgnoreCase("pending")) && imageFileName != null) {
                try {
                    File imageFile = new File(UPLOADS_DIR + imageFileName);
                    if (imageFile.exists()) {
                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("image", new FileSystemResource(imageFile));

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                                new HttpEntity<>(body, headers);

                        ResponseEntity<FlaskResponse> flaskResponse =
                                restTemplate.postForEntity(aiServiceUrl, requestEntity, FlaskResponse.class);

                        if (flaskResponse.getBody() != null) {
                            String mappedSeverity = mapClassToSeverity(flaskResponse.getBody().getClazz());
                            double confidence = flaskResponse.getBody().getConfidence();

                            // Save in AI model table
                            AImodel aiModel = new AImodel(imageFileName, mappedSeverity, confidence, "v1.0");
                            aiRepository.save(aiModel);

                            // Update appointment record
                            appt.setSeverity(mappedSeverity);
                            appointmentRepository.save(appt);

                            severity = mappedSeverity;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    severity = "pending";
                }
            }

            return new AIDto(imageFileName, severity != null ? severity : "pending");
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ------------------- PREDICT SEVERITY FOR SINGLE APPOINTMENT -------------------
    @PostMapping("/predict/{appointmentId}")
    public ResponseEntity<AIDto> predictSeverity(@PathVariable String appointmentId) {
        Appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        String imageFileName = appointment.getPotholePhoto();
        if (imageFileName == null || imageFileName.isEmpty()) {
            return ResponseEntity.badRequest().body(new AIDto(null, "No image found"));
        }

        File imageFile = new File(UPLOADS_DIR + imageFileName);
        if (!imageFile.exists()) {
            return ResponseEntity.badRequest().body(new AIDto(imageFileName, "Image file not found"));
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(imageFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        AIDto aiResult = new AIDto(imageFileName, "pending");

        try {
            ResponseEntity<FlaskResponse> response = restTemplate.postForEntity(
                    aiServiceUrl, requestEntity, FlaskResponse.class);

            if (response.getBody() != null) {
                String severity = mapClassToSeverity(response.getBody().getClazz());
                aiResult.setSeverity(severity);

                // Save AI prediction in DB
                AImodel aiModel = new AImodel(imageFileName, severity, response.getBody().getConfidence(), "v1.0");
                aiRepository.save(aiModel);

                // Update appointment severity
                appointment.setSeverity(severity);
                appointmentRepository.save(appointment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(aiResult);
    }

    // ------------------- HELPER: Map AI class to severity -------------------
    private String mapClassToSeverity(String clazz) {
        if (clazz == null) return "pending";
        switch (clazz.toLowerCase()) {
            case "minor_pothole": return "low";
            case "moderate_pothole": return "medium";
            case "major_pothole": return "high";
            default: return "pending";
        }
    }

    // ------------------- INNER CLASS: Flask Response -------------------
    private static class FlaskResponse {
        private String clazz;
        private double confidence;

        public String getClazz() { return clazz; }
        public void setClazz(String clazz) { this.clazz = clazz; }

        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
