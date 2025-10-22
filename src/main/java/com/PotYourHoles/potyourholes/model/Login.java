package com.PotYourHoles.potyourholes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users") // matches your MongoDB collection
public class Login {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String username;


    @Indexed(unique = true)
    private String email;

    private String password; // hashed password

    private Date createdAt = new Date(); // default to current date

    // Constructors
    public Login() {}

    public Login(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
