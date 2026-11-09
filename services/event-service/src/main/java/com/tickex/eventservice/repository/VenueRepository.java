package com.tickex.eventservice.repository;

import com.tickex.eventservice.model.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VenueRepository extends JpaRepository<VenueEntity, UUID> {
}
