package com.example.demo.dto;


public class PreferenceRequest {
    private String language;
    private float speechSpeed;
    private boolean vibrationEnabled;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getSpeechSpeed() {
        return speechSpeed;
    }

    public void setSpeechSpeed(float speechSpeed) {
        this.speechSpeed = speechSpeed;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }
}