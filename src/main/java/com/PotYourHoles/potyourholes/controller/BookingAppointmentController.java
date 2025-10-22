package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.dto.BookingAppDto;
import com.PotYourHoles.potyourholes.model.Appointments;
import com.PotYourHoles.potyourholes.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "${FRONTEND_URL:http://localhost:5173}") // dynamic frontend URL
public class BookingAppointmentController {

    @Autowired
    private AppointmentRepository repository;

    // ================= GET APPOINTMENTS (search + pagination) =================
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
            case "email":
                resultPage = repository.findByEmailContainingIgnoreCase(search, pageable);
                break;
            case "phone":
                resultPage = repository.findByPhoneContainingIgnoreCase(search, pageable);
                break;
            case "address.city":
                resultPage = repository.findByAddress_CityContainingIgnoreCase(search, pageable);
                break;
            case "address.state":
                resultPage = repository.findByAddress_StateContainingIgnoreCase(search, pageable);
                break;
            default:
                resultPage = repository.findByFullNameContainingIgnoreCase(search, pageable);
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
            throw new IllegalArgumentException("Full name, email, and phone are required");
        }

        Appointments app = new Appointments();
        app.setFullName(dto.getFullName());
        app.setEmail(dto.getEmail());
        app.setPhone(dto.getPhone());

        // Convert date safely
        if (dto.getDate() != null && !dto.getDate().isBlank()) {
            try {
                java.time.LocalDate localDate = java.time.LocalDate.parse(dto.getDate());
                app.setDate(java.sql.Date.valueOf(localDate));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd");
            }
        }

        Appointments.Address addr = new Appointments.Address(
                dto.getArea(), dto.getCity(), dto.getState(), dto.getPostCode()
        );
        app.setAddress(addr);

        // ===================== SAVE FILE =====================
        if (potholePhoto != null && !potholePhoto.isEmpty()) {
            String originalFilename = potholePhoto.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalFilename;

            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", fileName);

            // Copy file to uploads folder
            Files.copy(potholePhoto.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            // Save only filename in DB
            app.setPotholePhoto(fileName);
        }

        return repository.save(app);
    }

    // ================= DELETE APPOINTMENT =================
    @DeleteMapping("/{id}")
    public Map<String, String> deleteAppointment(@PathVariable String id) {
        repository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment deleted successfully");
        return response;
    }
}
