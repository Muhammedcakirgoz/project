package com.example.demo.controller;

import com.example.demo.dto.RouteRequest;
import com.example.demo.dto.RouteResponse;
import com.example.demo.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> generateRoute(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.generateRoute(request));
    }
}
