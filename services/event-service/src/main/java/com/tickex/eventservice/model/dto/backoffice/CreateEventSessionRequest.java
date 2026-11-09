package com.tickex.eventservice.model.dto.backoffice;

import com.tickex.eventservice.model.enums.EventSessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateEventSessionRequest {

    @NotNull
    private UUID eventId;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    private OffsetDateTime doorOpenTime;

    @NotNull
    private EventSessionStatus status;

    private OffsetDateTime salesStartTime;

    private OffsetDateTime salesEndTime;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}
