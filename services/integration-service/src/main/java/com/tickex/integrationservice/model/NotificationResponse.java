package com.tickex.integrationservice.model;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String status, Instant acceptedAt) {
}
