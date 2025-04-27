package com.example.demo.dto;

public class RouteResponse {
    private String summary;
    private int estimatedTime; // dakika

    // Parametresiz (default) constructor
    public RouteResponse() { }

    // İki parametreli constructor
    public RouteResponse(String summary, int estimatedTime) {
        this.summary        = summary;
        this.estimatedTime  = estimatedTime;
    }

    // Getter / setter’lar
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public int getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }


}