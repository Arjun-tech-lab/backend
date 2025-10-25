package com.PotYourHoles.potyourholes.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-ahar829hp-botme2121-2892s-projects.vercel.app/}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)// dynamic frontend URL
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
