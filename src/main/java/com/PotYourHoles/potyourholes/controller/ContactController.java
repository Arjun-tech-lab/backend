package com.PotYourHoles.potyourholes.controller;

import com.PotYourHoles.potyourholes.model.Contactus;
import com.PotYourHoles.potyourholes.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(
        origins = "${FRONTEND_URL:https://potyyourholes-gg85ccox0-botme2121-2892s-projects.vercel.app}",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)// Dynamic frontend URL
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // POST /api/contact
    @PostMapping
    public Contactus createContact(@RequestBody Contactus contact) {
        if (contact.getFirstName() == null || contact.getEmail() == null || contact.getMessage() == null) {
            throw new IllegalArgumentException("Please fill required fields");
        }
        return contactRepository.save(contact);
    }

    // GET /api/contact
    @GetMapping
    public List<Contactus> getAllContacts() {
        return contactRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt())) // sort by createdAt desc
                .toList();
    }
}
