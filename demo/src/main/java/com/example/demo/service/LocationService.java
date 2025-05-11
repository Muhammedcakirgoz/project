package com.example.demo.service;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;

import java.util.List;

public interface LocationService {
    // Android tarafı için kullanıcı e-posta adresini parametre olarak da geçelim:
    Location addLocation(LocationRequest req, String userEmail);

    List<Location> getLocations(String userEmail);

    void deleteLocation(Long locationId, String userEmail);
}