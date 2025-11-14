package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.dto.AIDto;
import com.PotYourHoles.potyourholes.model.AImodel;
import com.PotYourHoles.potyourholes.model.Appointments;
import com.PotYourHoles.potyourholes.repository.AIRepository;
import com.PotYourHoles.potyourholes.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes.vercel.app/}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class AIBookingController {

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Value("${AI_SERVICE_URL:http://localhost:5002}")
    private String aiServiceUrl;

    // ------------------- GET ALL APPOINTMENTS WITH AI SEVERITY -------------------
    @GetMapping("/appointments")
    public ResponseEntity<List<AIDto>> getAllAppointments() {

        List<Appointments> appointments = appointmentRepository.findAll();
        RestTemplate restTemplate = new RestTemplate();

        List<AIDto> response = appointments.stream().map(appt -> {

            String severity = appt.getSeverity();
            String imageUrl = appt.getPotholePhoto();

            // 🚨 Skip AI if image is not a real Cloudinary URL
            if (imageUrl != null && !imageUrl.startsWith("http")) {
                return new AIDto(imageUrl, severity != null ? severity : "pending");
            }

            // Only call AI if severity is missing or pending AND image is valid
            if ((severity == null || severity.equalsIgnoreCase("pending")) && imageUrl != null) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    Map<String, String> body = new HashMap<>();
                    body.put("image_url", imageUrl);

                    HttpEntity<Map<String, String>> request =
                            new HttpEntity<>(body, headers);

                    ResponseEntity<FlaskResponse> flaskResponse =
                            restTemplate.postForEntity(aiServiceUrl + "/predict",
                                    request, FlaskResponse.class);

                    if (flaskResponse.getBody() != null) {
                        String mappedSeverity = mapClassToSeverity(flaskResponse.getBody().getClazz());
                        double confidence = flaskResponse.getBody().getConfidence();

                        AImodel aiModel = new AImodel(imageUrl, mappedSeverity, confidence, "v1.0");
                        aiRepository.save(aiModel);

                        appt.setSeverity(mappedSeverity);
                        appointmentRepository.save(appt);

                        severity = mappedSeverity;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    severity = "pending";
                }
            }

            return new AIDto(imageUrl, severity);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ------------------- PREDICT SINGLE APPOINTMENT -------------------
    @PostMapping("/predict/{id}")
    public ResponseEntity<AIDto> predictSeverity(@PathVariable String id) {

        Appointments appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        String imageUrl = appt.getPotholePhoto();

        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(new AIDto(null, "No image found"));
        }

        // 🚨 Skip AI for old filename-based images
        if (!imageUrl.startsWith("http")) {
            return ResponseEntity.ok(new AIDto(imageUrl, "pending"));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("image_url", imageUrl);

            HttpEntity<Map<String, String>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<FlaskResponse> response =
                    restTemplate.postForEntity(aiServiceUrl + "/predict",
                            requestEntity, FlaskResponse.class);

            if (response.getBody() != null) {
                String severity = mapClassToSeverity(response.getBody().getClazz());

                appt.setSeverity(severity);
                appointmentRepository.save(appt);

                AImodel model = new AImodel(imageUrl, severity,
                        response.getBody().getConfidence(), "v1.0");

                aiRepository.save(model);

                return ResponseEntity.ok(new AIDto(imageUrl, severity));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(new AIDto(imageUrl, "pending"));
    }

    // ------------------- MAP AI CLASS -------------------
    private String mapClassToSeverity(String clazz) {
        if (clazz == null) return "pending";

        switch (clazz.toLowerCase()) {
            case "minor_pothole": return "low";
            case "moderate_pothole": return "medium";
            case "major_pothole": return "high";
            default: return "pending";
        }
    }

    // ------------------- INNER CLASS -------------------
    private static class FlaskResponse {
        private String clazz;
        private double confidence;

        public String getClazz() { return clazz; }
        public void setClazz(String clazz) { this.clazz = clazz; }

        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
