package com.PotYourHoles.potyourholes.dto;

public class BookingAppDto {
    private String fullName;
    private String email;
    private String phone;
    private String date; // String from frontend, convert later
    private String area;
    private String city;
    private String state;
    private String postCode;

    // Getters & Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
}
