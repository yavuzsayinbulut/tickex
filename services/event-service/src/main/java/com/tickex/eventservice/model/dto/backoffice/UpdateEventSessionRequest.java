package com.tickex.eventservice.model.dto.backoffice;

import com.tickex.eventservice.model.enums.EventSessionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class UpdateEventSessionRequest {

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private OffsetDateTime doorOpenTime;

    private EventSessionStatus status;

    private OffsetDateTime salesStartTime;

    private OffsetDateTime salesEndTime;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}
