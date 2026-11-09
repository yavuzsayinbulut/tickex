package com.tickex.eventservice.repository;

import com.tickex.eventservice.model.entity.EventEntity;
import com.tickex.eventservice.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {

    Optional<EventEntity> findBySlug(String slug);

    Page<EventEntity> findByStatusAndStartDateGreaterThanEqual(
            EventStatus status,
            LocalDate startDate,
            Pageable pageable
    );

    List<EventEntity> findTop10ByStatusAndFeaturedOrderByStartDateAsc(EventStatus status, boolean featured);
}
