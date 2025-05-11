package com.example.demo.dto;

import java.util.List;

// Python’dan dönen yapıya göre özelleştir
public class TransitResponse {
    private List<String> steps;
    private int duration;
    // ... diğer alanlar, getters/setters


    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
