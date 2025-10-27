package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.dto.BookingAppDto;
import com.PotYourHoles.potyourholes.model.Appointments;
import com.PotYourHoles.potyourholes.repository.AppointmentRepository;
import com.PotYourHoles.potyourholes.services.EmailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-ahar829hp-botme2121-2892s-projects.vercel.app}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class BookingAppointmentController {

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private EmailServices emailServices;

    @Value("${UPLOAD_DIR:uploads}")
    private String uploadDir;

    // ================= GET APPOINTMENTS =================
    @GetMapping
    public Map<String, Object> getAppointments(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "searchField", defaultValue = "fullName") String searchField,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Appointments> resultPage;

        switch (searchField) {
            case "email" -> resultPage = repository.findByEmailContainingIgnoreCase(search, pageable);
            case "phone" -> resultPage = repository.findByPhoneContainingIgnoreCase(search, pageable);
            case "address.city" -> resultPage = repository.findByAddress_CityContainingIgnoreCase(search, pageable);
            case "address.state" -> resultPage = repository.findByAddress_StateContainingIgnoreCase(search, pageable);
            default -> resultPage = repository.findByFullNameContainingIgnoreCase(search, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", resultPage.getContent());
        response.put("total", resultPage.getTotalElements());
        return response;
    }

    // ================= POST APPOINTMENT =================
    @PostMapping
    public Appointments addAppointment(
            @RequestPart("data") BookingAppDto dto,
            @RequestPart(value = "potholePhoto", required = false) MultipartFile potholePhoto
    ) throws IOException {

        if (dto.getFullName() == null || dto.getEmail() == null || dto.getPhone() == null) {
            throw new IllegalArgumentException("Full name, email, and phone are required.");
        }

        Appointments app = new Appointments();
        app.setFullName(dto.getFullName());
        app.setEmail(dto.getEmail());
        app.setPhone(dto.getPhone());

        if (dto.getDate() != null && !dto.getDate().isBlank()) {
            try {
                java.time.LocalDate localDate = java.time.LocalDate.parse(dto.getDate());
                app.setDate(java.sql.Date.valueOf(localDate));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd.");
            }
        }

        Appointments.Address addr = new Appointments.Address(
                dto.getArea(), dto.getCity(), dto.getState(), dto.getPostCode()
        );
        app.setAddress(addr);

        // ===================== SAVE FILE =====================
        if (potholePhoto != null && !potholePhoto.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir)); // ✅ Ensure directory exists
            String originalFilename = potholePhoto.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "photo.jpg");
            Path uploadPath = Paths.get(uploadDir, fileName);
            Files.copy(potholePhoto.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            app.setPotholePhoto(fileName);
        }

        Appointments savedAppointment = repository.save(app);

        // ===================== SEND EMAIL =====================
        try {
            emailServices.sendThankYouEmail(dto.getEmail(), dto.getFullName());
        } catch (Exception e) {
            System.err.println(" Failed to send thank-you email: " + e.getMessage());
        }

        return savedAppointment;
    }

    // ================= DELETE APPOINTMENT =================
    @DeleteMapping("/{id}")
    public Map<String, String> deleteAppointment(@PathVariable String id) {
        repository.deleteById(id);
        return Map.of("message", "Appointment deleted successfully");
    }
}
