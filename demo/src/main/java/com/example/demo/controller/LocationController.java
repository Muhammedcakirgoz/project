package com.example.demo.controller;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;
import com.example.demo.service.LocationService;
<<<<<<< HEAD
import jakarta.validation.Valid;
import java.util.List;
=======
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
import jakarta.validation.Valid;
import java.util.List;

>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
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
<<<<<<< HEAD
        
        return ResponseEntity.noContent().build();
    }
}
=======
        return ResponseEntity.noContent().build();
    }
}
>>>>>>> 21c10867da6cdac9fb7ebc9da7ecf962ca6f6e3f
