package com.tickex.ticketingservice.model.entity;

import com.tickex.ticketingservice.model.enums.TicketTypeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket_type",
       uniqueConstraints = @UniqueConstraint(name = "uk_ticket_type_session_code", columnNames = {"event_session_id", "code"}))
public class TicketTypeEntity extends BaseEntity {

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_session_id", nullable = false)
    private UUID eventSessionId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "base_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal basePrice;

    @Column(name = "service_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "min_per_order", nullable = false)
    private int minPerOrder = 1;

    @Column(name = "max_per_order", nullable = false)
    private int maxPerOrder = 10;

    @Column(name = "refundable", nullable = false)
    private boolean refundable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TicketTypeStatus status = TicketTypeStatus.ACTIVE;
}
