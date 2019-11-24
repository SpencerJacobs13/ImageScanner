package com.example.personalproject;

public class ImageLabel {
    float confidence;
    String label;
    String entityId;

    public ImageLabel(float confidence, String label, String entityId) {
        this.confidence = confidence;
        this.label = label;
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "ID: " + entityId + ". Confidence: " + confidence;
    }
}
