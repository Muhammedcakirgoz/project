package com.example.demo.dto;

public class TransitRequest {
    private String origin;
    private String destination;
    // …
    // Parametresiz ctor (Spring için gerekli)
    public TransitRequest() {}

    // ✔️ Sizin çağrınıza uyacak ctor:
    public TransitRequest(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
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
}

