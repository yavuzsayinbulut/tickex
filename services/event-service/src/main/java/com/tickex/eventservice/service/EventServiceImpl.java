package com.tickex.eventservice.service;

import com.tickex.eventservice.exception.AccessDeniedDomainException;
import com.tickex.eventservice.exception.ResourceNotFoundException;
import com.tickex.eventservice.mapper.EventMapper;
import com.tickex.eventservice.mapper.EventSessionMapper;
import com.tickex.eventservice.model.dto.backoffice.CreateEventRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventRequest;
import com.tickex.eventservice.model.dto.EventDetailDto;
import com.tickex.eventservice.model.dto.EventFilter;
import com.tickex.eventservice.model.dto.EventListItemDto;
import com.tickex.eventservice.model.dto.EventSessionSummaryDto;
import com.tickex.eventservice.model.entity.EventEntity;
import com.tickex.eventservice.model.entity.EventSessionEntity;
import com.tickex.eventservice.model.entity.EventTagEntity;
import com.tickex.eventservice.model.entity.TagEntity;
import com.tickex.eventservice.model.enums.EventStatus;
import com.tickex.eventservice.repository.*;
import com.tickex.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventSessionRepository eventSessionRepository;
    private final TagRepository tagRepository;
    private final EventTagRepository eventTagRepository;

    @Override
    public EventDetailDto getEventDetail(UUID eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        var venue = venueRepository.findById(event.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + event.getVenueId()));

        List<EventSessionSummaryDto> sessions = eventSessionRepository
                .findByEventIdOrderByStartTimeAsc(eventId)
                .stream()
                .map(EventSessionMapper::toSummaryDto)
                .toList();

        List<String> tags = getTagsForEvent(eventId);

        return EventMapper.toDetailDto(event, venue, sessions, tags);
    }

    @Override
    public Page<EventListItemDto> searchEvents(EventFilter filter, Pageable pageable) {
        // Basit bir filtre: PUBLISHED ve bugün sonrasındaki event’ler
        LocalDate today = LocalDate.now();
        Page<EventEntity> page = eventRepository
                .findByStatusAndStartDateGreaterThanEqual(EventStatus.PUBLISHED, today, pageable);

        return page.map(event -> {
            var venue = venueRepository.findById(event.getVenueId()).orElse(null);
            return EventMapper.toListItemDto(event, venue);
        });
    }

    @Override
    public EventDetailDto createEvent(CreateEventRequest request, UUID organizerId) {
        validateDates(request.getStartDate(), request.getEndDate());

        EventEntity event = EventEntity.builder()
                .slug(generateSlug(request.getTitle()))
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .organizerId(organizerId)
                .venueId(request.getVenueId())
                .status(EventStatus.DRAFT)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .featured(request.isFeatured())
                .build();

        EventEntity saved = eventRepository.save(event);

        saveTags(saved.getId(), request.getTags());

        return getEventDetail(saved.getId());
    }

    @Override
    public EventDetailDto updateEvent(UUID eventId, UpdateEventRequest request, UUID currentUserId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        validateOwnership(event, currentUserId);

        if (StringUtils.hasText(request.getTitle())) {
            event.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getCategory() != null) {
            event.setCategory(request.getCategory());
        }

        if (request.getVenueId() != null) {
            event.setVenueId(request.getVenueId());
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            validateDates(request.getStartDate(), request.getEndDate());
            event.setStartDate(request.getStartDate());
            event.setEndDate(request.getEndDate());
        }

        if (request.getFeatured() != null) {
            event.setFeatured(request.getFeatured());
        }

        EventEntity saved = eventRepository.save(event);

        if (request.getTags() != null) {
            // Tüm tagleri resetle ve yeniden kaydet
            deleteTags(saved.getId());
            saveTags(saved.getId(), request.getTags());
        }

        return getEventDetail(saved.getId());
    }

    @Override
    public void changeStatus(UUID eventId, EventStatus newStatus, UUID currentUserId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        validateOwnership(event, currentUserId);

        event.setStatus(newStatus);
        eventRepository.save(event);
    }

    @Override
    public List<EventListItemDto> getFeaturedEvents() {
        return eventRepository
                .findTop10ByStatusAndFeaturedOrderByStartDateAsc(EventStatus.PUBLISHED, true)
                .stream()
                .map(event -> {
                    var venue = venueRepository.findById(event.getVenueId()).orElse(null);
                    return EventMapper.toListItemDto(event, venue);
                })
                .toList();
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
    }

    private void validateOwnership(EventEntity event, UUID currentUserId) {
        // ADMIN kontrolü burada yok; Security tarafında ROLE_ADMIN için bypass kurgulayabilirsin.
        if (!Objects.equals(event.getOrganizerId(), currentUserId)) {
            throw new AccessDeniedDomainException("You are not the organizer of this event");
        }
    }

    private String generateSlug(String title) {
        String base = title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        String random = UUID.randomUUID().toString().substring(0, 8);
        return base + "-" + random;
    }

    private void saveTags(UUID eventId, List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        List<TagEntity> tagEntities = tags.stream()
                .filter(StringUtils::hasText)
                .map(code -> tagRepository.findByCode(code)
                        .orElseGet(() -> tagRepository.save(
                                TagEntity.builder()
                                        .code(code)
                                        .displayName(code)
                                        .build()
                        )))
                .toList();

        List<EventTagEntity> eventTags = tagEntities.stream()
                .map(tag -> EventTagEntity.builder()
                        .eventId(eventId)
                        .tagId(tag.getId())
                        .build())
                .toList();

        eventTagRepository.saveAll(eventTags);
    }

    private void deleteTags(UUID eventId) {
        List<EventTagEntity> list = eventTagRepository.findByEventId(eventId);
        eventTagRepository.deleteAll(list);
    }

    private List<String> getTagsForEvent(UUID eventId) {
        List<EventTagEntity> ets = eventTagRepository.findByEventId(eventId);
        if (ets.isEmpty()) return List.of();

        Map<UUID, TagEntity> tagMap = tagRepository.findAllById(
                        ets.stream().map(EventTagEntity::getTagId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(TagEntity::getId, t -> t));

        return ets.stream()
                .map(et -> {
                    TagEntity tag = tagMap.get(et.getTagId());
                    return tag != null ? tag.getCode() : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
