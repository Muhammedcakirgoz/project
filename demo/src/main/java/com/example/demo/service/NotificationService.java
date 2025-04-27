package com.example.demo.service;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendNotification(NotificationRequest req);
    List<NotificationResponse> getUserNotifications();
}