package com.example.demo.service;

import com.example.demo.dto.RouteRequest;
import com.example.demo.dto.RouteResponse;

public interface RouteService {
    RouteResponse generateRoute(RouteRequest request);
}