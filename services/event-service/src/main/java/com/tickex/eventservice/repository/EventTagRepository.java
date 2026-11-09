package com.tickex.eventservice.repository;

import com.tickex.eventservice.model.entity.EventTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventTagRepository extends JpaRepository<EventTagEntity, UUID> {

    List<EventTagEntity> findByEventId(UUID eventId);
}
