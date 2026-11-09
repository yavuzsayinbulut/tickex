package com.tickex.ticketingservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket_check_in")
public class TicketCheckInEntity extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Column(name = "operator_user_id")
    private java.util.UUID operatorUserId;

    @Column(name = "gate", length = 80)
    private String gate;

    @Column(name = "device_id", length = 120)
    private String deviceId;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "SUCCESS";

    @Column(name = "checked_in_at", nullable = false)
    private OffsetDateTime checkedInAt = OffsetDateTime.now();
}
