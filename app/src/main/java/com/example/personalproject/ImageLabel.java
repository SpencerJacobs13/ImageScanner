package com.example.personalproject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

        return entityId + " with " + new DecimalFormat("#0.00%").format(confidence) + " confidence.";
    }
}
