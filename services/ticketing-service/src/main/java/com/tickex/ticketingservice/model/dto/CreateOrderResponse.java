package com.tickex.ticketingservice.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String status,
        String currency,
        BigDecimal totalAmount,
        OffsetDateTime expiresAt,
        List<OrderItem> items
) {
    public record OrderItem(UUID ticketTypeId, int quantity, BigDecimal lineTotal) {}
}
