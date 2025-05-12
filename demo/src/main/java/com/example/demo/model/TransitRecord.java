package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class TransitRecord {
    @Id @GeneratedValue
    private Long id;
    private String origin;
    private String destination;

    @Lob
    private String payload; // Python'dan gelen ham JSON

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}