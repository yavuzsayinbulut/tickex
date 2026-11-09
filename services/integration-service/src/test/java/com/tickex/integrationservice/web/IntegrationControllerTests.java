package com.tickex.integrationservice.web;

import com.tickex.integrationservice.model.NotificationRequest;
import com.tickex.integrationservice.model.NotificationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationControllerTests {

    @Test
    void forwardsNotificationToGateway() {
        var expected = new NotificationResponse(UUID.randomUUID(), "QUEUED", Instant.now());
        var controller = new IntegrationController(request -> expected);

        var response = controller.forwardNotification(
                new NotificationRequest("user-1", "Ticket ready", "Your ticket is ready"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
