package com.tickex.ticketingservice.repository;

import com.tickex.ticketingservice.model.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> { }
