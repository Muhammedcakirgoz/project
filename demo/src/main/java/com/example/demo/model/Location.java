package com.example.demo.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import java.util.Date;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
=======
import lombok.*;
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitude;
<<<<<<< HEAD
    
=======
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
    @Column(nullable = false)
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
<<<<<<< HEAD
    private String name;      // Örn. "Ev", "Ofis" vb.
    
    @Column(nullable = false)
    private String address;  // Adres bilgisi
    
    private String notes;    // Notlar
    
    @Column(nullable = false)
    @CreationTimestamp      // Otomatik olarak şu anki tarih/saat eklenecek
    private Date timestamp;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
=======
    private String name;      // Örn. “Ev”, “Ofis” vb.
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }
<<<<<<< HEAD
    
=======
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}