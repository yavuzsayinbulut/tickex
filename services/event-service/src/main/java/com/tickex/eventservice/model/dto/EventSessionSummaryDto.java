package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class EventSessionSummaryDto {
    UUID id;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    String status;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
