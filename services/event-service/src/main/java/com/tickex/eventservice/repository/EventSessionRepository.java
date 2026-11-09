package com.tickex.eventservice.repository;

import com.tickex.eventservice.model.entity.EventSessionEntity;
import com.tickex.eventservice.model.enums.EventSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventSessionRepository extends JpaRepository<EventSessionEntity, UUID> {

    List<EventSessionEntity> findByEventIdOrderByStartTimeAsc(UUID eventId);

    List<EventSessionEntity> findByEventIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            UUID eventId,
            EventSessionStatus status,
            OffsetDateTime after
    );
}
