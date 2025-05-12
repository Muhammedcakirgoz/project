package com.example.demo.service;

import com.example.demo.model.TransitResponse;

public interface TransitService {
    TransitResponse getTransitInfo(String origin, String destination);
}