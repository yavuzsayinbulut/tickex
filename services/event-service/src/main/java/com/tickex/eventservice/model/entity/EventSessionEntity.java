package com.tickex.eventservice.model.entity;

import com.tickex.eventservice.model.enums.EventSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSessionEntity extends BaseEntity {

    @Column(name = "event_id", nullable = false, columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "door_open_time")
    private OffsetDateTime doorOpenTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EventSessionStatus status;

    @Column(name = "sales_start_time")
    private OffsetDateTime salesStartTime;

    @Column(name = "sales_end_time")
    private OffsetDateTime salesEndTime;

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;
}
