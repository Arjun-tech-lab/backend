package com.PotYourHoles.potyourholes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "contacts") // matches your MongoDB collection
public class Contactus{

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String message;

    private Date createdAt = new Date(); // default to current date

    // Constructors
    public Contactus() {} // empty constructor required by Spring

    public Contactus(String firstName, String lastName, String email, String message) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.message = message;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
