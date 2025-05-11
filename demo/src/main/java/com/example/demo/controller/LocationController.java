package com.example.demo.controller;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final UserRepository userRepository;

    public LocationController(LocationRepository locationRepository, LocationService locationService, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.locationService = locationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveLocation(@RequestBody LocationRequest locationRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Location location = new Location();
        location.setLatitude(locationRequest.getLatitude());
        location.setLongitude(locationRequest.getLongitude());
        location .setUser(user);
        locationRepository.save(location);
        return ResponseEntity.ok(location);
    }
}