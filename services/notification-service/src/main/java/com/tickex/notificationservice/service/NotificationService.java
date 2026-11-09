package com.tickex.notificationservice.service;

import com.tickex.notificationservice.model.NotificationRequest;
import com.tickex.notificationservice.model.NotificationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificationService {

    public NotificationResponse accept(NotificationRequest request) {
        return new NotificationResponse(UUID.randomUUID(), "QUEUED", Instant.now());
    }
}
