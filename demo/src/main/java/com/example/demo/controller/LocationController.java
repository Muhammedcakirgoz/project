package com.example.demo.controller;

import com.example.demo.dto.LocationRequest;
import com.example.demo.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<String> saveLocation(@RequestBody LocationRequest request) {
        locationService.saveLocation(request);
        return ResponseEntity.ok("Konum başarıyla kaydedildi.");
    }
}