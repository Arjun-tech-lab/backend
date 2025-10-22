package com.PotYourHoles.potyourholes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "appointments")
public class Appointments {

    @Id
    private String id;

    private String fullName;
    private String phone;
    private String email;
    private Date date;
    private Address address;
    private String potholePhoto;
    private Date createdAt = new Date();

    // ✅ Add severity field
    private String severity;

    public void setStatus(String status) {
    }

    // ===== Nested Address class =====
    public static class Address {
        private String area;
        private String city;
        private String state;
        private String postCode;

        public Address() {}
        public Address(String area, String city, String state, String postCode) {
            this.area = area;
            this.city = city;
            this.state = state;
            this.postCode = postCode;
        }

        // Getters & Setters
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPostCode() { return postCode; }
        public void setPostCode(String postCode) { this.postCode = postCode; }
    }

    // Getters & Setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getPotholePhoto() { return potholePhoto; }
    public void setPotholePhoto(String potholePhoto) { this.potholePhoto = potholePhoto; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // ✅ Severity getters/setters
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
