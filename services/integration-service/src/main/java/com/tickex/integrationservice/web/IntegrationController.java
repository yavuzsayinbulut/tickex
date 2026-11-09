package com.tickex.integrationservice.web;

import com.tickex.integrationservice.gateway.NotificationGateway;
import com.tickex.integrationservice.model.NotificationRequest;
import com.tickex.integrationservice.model.NotificationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    private final NotificationGateway notificationGateway;

    public IntegrationController(NotificationGateway notificationGateway) {
        this.notificationGateway = notificationGateway;
    }

    @PostMapping("/notifications")
    public ResponseEntity<NotificationResponse> forwardNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.accepted().body(notificationGateway.send(request));
    }
}
