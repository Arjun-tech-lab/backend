package com.PotYourHoles.potyourholes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "testdata")
public class TestData {

    @Id
    private String id;
    private String name;

    public TestData() {} // empty constructor required by Spring

    public TestData(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
