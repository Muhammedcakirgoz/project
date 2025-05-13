package com.example.demo.controller;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;
import com.example.demo.service.LocationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(
            @Valid @RequestBody LocationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        Location created = locationService.addLocation(req, userDetails.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        Location updated = locationService.updateLocation(id, req, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Location>> listLocations(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Location> list = locationService.getLocations(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable String name,
            @AuthenticationPrincipal UserDetails userDetails) {

        locationService.deleteLocationByName(name, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}