package com.example.demo.dto;

import com.example.demo.model.NotificationType;

import java.time.Instant;

public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Instant timestamp;
    // constructor, getters


    public NotificationResponse(Long id, NotificationType type, String title, String message, Instant timestamp) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}