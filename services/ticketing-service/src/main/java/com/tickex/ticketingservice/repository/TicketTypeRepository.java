package com.tickex.ticketingservice.repository;

import com.tickex.ticketingservice.model.entity.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, UUID> {
    List<TicketTypeEntity> findAllByEventSessionId(UUID eventSessionId);
}
