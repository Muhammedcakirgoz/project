package com.example.demo.controller;

import com.example.demo.model.TransitResponse;
import com.example.demo.service.TransitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transit")
@CrossOrigin(origins = "*")
public class TransitController {

    private final TransitService transitService;
    
    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }

    @GetMapping
    public ResponseEntity<TransitResponse> getTransitInfo(
            @RequestParam String origin,
            @RequestParam String destination) {

        TransitResponse response = transitService.getTransitInfo(origin, destination);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/directions")
    public ResponseEntity<TransitResponse> getTransitDirections(
            @RequestParam String origin,
            @RequestParam String destination) {

        TransitResponse response = transitService.getTransitInfo(origin, destination);
        return ResponseEntity.ok(response);
    }
}