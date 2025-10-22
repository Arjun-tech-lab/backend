package com.PotYourHoles.potyourholes.dto;

public class AIDto {
    private String imagePath;
    private String severity;

    public AIDto() {}

    public AIDto(String imagePath, String severity) {
        this.imagePath = imagePath;
        this.severity = severity;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
