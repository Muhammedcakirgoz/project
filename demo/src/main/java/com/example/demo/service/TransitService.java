package com.example.demo.service;

import com.example.demo.dto.TransitRequest;
import com.example.demo.dto.TransitResponse;

public interface TransitService {
    TransitResponse getAndSaveTransit(TransitRequest req);
}