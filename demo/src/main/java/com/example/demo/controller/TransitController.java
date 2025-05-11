package com.example.demo.controller;

import com.example.demo.dto.TransitRequest;
import com.example.demo.dto.TransitResponse;
import com.example.demo.service.TransitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transit")
public class TransitController {

    private final TransitService transitService;

    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }

    @GetMapping
    public ResponseEntity<TransitResponse> getTransitInfo(
            @RequestParam String origin,
            @RequestParam String destination) {

        TransitRequest req = new TransitRequest(origin, destination);
        TransitResponse resp = transitService.getAndSaveTransit(req);
        return ResponseEntity.ok(resp);
    }
}
