package com.tickex.ticketingservice.model.entity;

import com.tickex.ticketingservice.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket",
       indexes = {
           @Index(name = "idx_ticket_owner", columnList = "owner_user_id"),
           @Index(name = "idx_ticket_session", columnList = "event_session_id,status")
       })
public class TicketEntity extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_session_id", nullable = false)
    private UUID eventSessionId;

    @Column(name = "ticket_type_id", nullable = false)
    private UUID ticketTypeId;

    @Column(name = "serial_no", nullable = false, unique = true, length = 80)
    private String serialNo;

    @Column(name = "qr_token_hash", nullable = false, length = 255)
    private String qrTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TicketStatus status = TicketStatus.ISSUED;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt = OffsetDateTime.now();

    @Column(name = "used_at")
    private OffsetDateTime usedAt;
}
