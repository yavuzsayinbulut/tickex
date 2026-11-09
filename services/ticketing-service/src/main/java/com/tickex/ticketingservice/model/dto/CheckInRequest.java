package com.tickex.ticketingservice.model.dto;

public record CheckInRequest(
        String gate,
        String deviceId
) {}
