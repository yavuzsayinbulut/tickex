package com.tickex.notificationservice.service;

import com.tickex.notificationservice.model.NotificationRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTests {

    private final NotificationService service = new NotificationService();

    @Test
    void acceptsNotificationForDelivery() {
        var response = service.accept(
                new NotificationRequest("user-1", "Ticket ready", "Your ticket is ready"));

        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo("QUEUED");
        assertThat(response.acceptedAt()).isNotNull();
    }
}
