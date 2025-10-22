package com.PotYourHoles.potyourholes.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "${FRONTEND_URL:http://localhost:5173}") // dynamic frontend URL
public class PotHoleAIController {

    @GetMapping("/model")
    public ResponseEntity<FileSystemResource> getModel() {
        File file = new File("ai/model.json"); // path relative to your project root
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new FileSystemResource(file));
    }
}
