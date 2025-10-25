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
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-ahar829hp-botme2121-2892s-projects.vercel.app/}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)// dynamic frontend URL
public class LoginController {

    @Autowired
    private LoginRepository loginRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ------------------- SIGNUP -------------------
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        String passwordConfirmation = body.get("password_confirmation");

        Map<String, String> response = new HashMap<>();

        // Validation
        if (name == null || username == null || email == null || password == null || passwordConfirmation == null) {
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (!password.equals(passwordConfirmation)) {
            response.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user exists
        Optional<Login> existingUser = loginRepository.findByEmailOrUsername(email, username);
        if (existingUser.isPresent()) {
            response.put("message", "User already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // Create user
        Login newUser = new Login();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        loginRepository.save(newUser);

        response.put("message", "User registered successfully");
        return ResponseEntity.status(201).body(response);
    }

    // ------------------- LOGIN -------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        String password = body.get("password");

        Map<String, Object> response = new HashMap<>();

        if ((email == null && username == null) || password == null) {
            response.put("message", "Email/Username and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Login> userOptional;
        if (email != null && !email.isEmpty()) {
            userOptional = loginRepository.findByEmail(email);
        } else {
            userOptional = loginRepository.findByUsername(username);
        }

        if (userOptional.isEmpty()) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }

        Login user = userOptional.get();

        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        if (!passwordMatches) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());

        response.put("message", "Login successful");
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    // ------------------- OPTIONAL: Test CORS -------------------
    @GetMapping("/test")
    public String test() {
        return "CORS is working!";
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
