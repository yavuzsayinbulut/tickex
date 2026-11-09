package com.tickex.ticketingservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket_inventory",
       uniqueConstraints = @UniqueConstraint(name = "uk_ticket_inventory_type", columnNames = {"ticket_type_id"}))
public class TicketInventoryEntity extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketTypeEntity ticketType;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private int soldQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;
}
