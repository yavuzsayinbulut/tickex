package com.tickex.integrationservice.gateway;

import com.tickex.integrationservice.model.NotificationRequest;
import com.tickex.integrationservice.model.NotificationResponse;

@FunctionalInterface
public interface NotificationGateway {

    NotificationResponse send(NotificationRequest request);
}
