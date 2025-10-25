package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.model.Login;
import com.PotYourHoles.potyourholes.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/signup")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-ahar829hp-botme2121-2892s-projects.vercel.app/}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class SignUpController {

    @Autowired
    private LoginRepository loginRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ------------------- SIGNUP -------------------
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        String passwordConfirmation = body.get("password_confirmation");

        Map<String, String> response = new HashMap<>();

        // Validate input
        if (name == null || username == null || email == null || password == null || passwordConfirmation == null) {
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (!password.equals(passwordConfirmation)) {
            response.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user already exists
        Optional<Login> existingUser = loginRepository.findByEmailOrUsername(email, username);
        if (existingUser.isPresent()) {
            response.put("message", "User already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // Save new user
        Login newUser = new Login(name, username, email, hashedPassword);
        loginRepository.save(newUser);

        response.put("message", "User registered successfully");
        return ResponseEntity.status(201).body(response);
    }
}
