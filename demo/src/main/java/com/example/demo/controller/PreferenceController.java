package com.example.demo.controller;

import com.example.demo.dto.PreferenceRequest;
import com.example.demo.model.Preference;
import com.example.demo.service.PreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping
    public ResponseEntity<Preference> save(@RequestBody PreferenceRequest request) {
        return ResponseEntity.ok(preferenceService.savePreference(request));
    }

    @GetMapping
    public ResponseEntity<Preference> get() {
        return ResponseEntity.ok(preferenceService.getLastPreference());
    }
}