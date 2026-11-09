package com.tickex.ticketingservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmPaymentRequest(
        @NotBlank String provider,
        String providerPaymentId,
        String rawPayloadJson
) {}
