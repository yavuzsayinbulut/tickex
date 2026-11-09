package com.tickex.ticketingservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID eventSessionId,
        @NotEmpty @Valid List<Item> items,
        String promoCode,
        String idempotencyKey
) {
    public record Item(
            @NotNull UUID ticketTypeId,
            @Min(1) @Max(20) int quantity
    ) {}
}
