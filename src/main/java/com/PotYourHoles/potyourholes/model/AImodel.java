package com.PotYourHoles.potyourholes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "ai_predictions")
public class AImodel {

    @Id
    private String id;
    private String imagePath;
    private String severity;
    private Double confidence;
    private String modelVersion;
    private Date processedAt = new Date();

    public AImodel() {}

    public AImodel(String imagePath, String severity, Double confidence, String modelVersion) {
        this.imagePath = imagePath;
        this.severity = severity;
        this.confidence = confidence;
        this.modelVersion = modelVersion;
        this.processedAt = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public Date getProcessedAt() { return processedAt; }
    public void setProcessedAt(Date processedAt) { this.processedAt = processedAt; }
}
