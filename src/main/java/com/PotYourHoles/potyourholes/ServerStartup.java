package com.PotYourHoles.potyourholes;

import com.PotYourHoles.potyourholes.repository.TestDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ServerStartup implements CommandLineRunner {

    @Autowired
    private TestDataRepository repo; // any repository works

    @Override
    public void run(String... args) {
        try {
            long count = repo.count(); // tries to query MongoDB
            System.out.println("✅ MongoDB connected successfully! Collection has " + count + " documents.");
        } catch (Exception e) {
            System.out.println("❌ MongoDB connection failed: " + e.getMessage());
        }
    }
}
