package com.tickex.integrationservice.gateway;

import com.tickex.integrationservice.model.NotificationRequest;
import com.tickex.integrationservice.model.NotificationResponse;
import com.tickex.integrationservice.web.NotificationUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestNotificationGateway implements NotificationGateway {

    private final RestClient restClient;

    public RestNotificationGateway(
            RestClient.Builder builder,
            @Value("${tickex.notification.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        try {
            var response = restClient.post()
                    .uri("/api/v1/notifications")
                    .body(request)
                    .retrieve()
                    .body(NotificationResponse.class);

            if (response == null) {
                throw new NotificationUnavailableException();
            }
            return response;
        } catch (RestClientException exception) {
            throw new NotificationUnavailableException(exception);
        }
    }
}
