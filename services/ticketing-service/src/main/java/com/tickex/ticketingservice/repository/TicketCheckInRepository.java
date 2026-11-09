package com.tickex.ticketingservice.repository;

import com.tickex.ticketingservice.model.entity.TicketCheckInEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketCheckInRepository extends JpaRepository<TicketCheckInEntity, UUID> { }
