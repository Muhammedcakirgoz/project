package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
//@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;
    private float speechSpeed;
    private boolean vibrationEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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