package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationRequest {
    @NotBlank
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
<<<<<<< HEAD
    
    private String address;
    
    private String notes;
    
    private String timestamp;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
=======
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

<<<<<<< HEAD
=======
    public void setLatitude(@NotNull Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(@NotNull Double longitude) {
        this.longitude = longitude;
    }

>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

<<<<<<< HEAD
    public void setLatitude(@NotNull Double latitude) {
        this.latitude = latitude;
    }

=======
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

<<<<<<< HEAD
    public void setLongitude(@NotNull Double longitude) {
        this.longitude = longitude;
    }
}
=======

}
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
