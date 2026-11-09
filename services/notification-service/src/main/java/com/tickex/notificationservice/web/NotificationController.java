package com.tickex.notificationservice.web;

import com.tickex.notificationservice.model.NotificationRequest;
import com.tickex.notificationservice.model.NotificationResponse;
import com.tickex.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> accept(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.accepted().body(notificationService.accept(request));
    }
}
