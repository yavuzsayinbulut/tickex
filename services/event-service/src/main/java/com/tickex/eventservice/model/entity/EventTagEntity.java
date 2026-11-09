package com.tickex.eventservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "event_tag",
       uniqueConstraints = @UniqueConstraint(name = "uk_event_tag_event_id_tag_id",
                                             columnNames = {"event_id", "tag_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTagEntity extends BaseEntity {

    @Column(name = "event_id", nullable = false, columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "tag_id", nullable = false, columnDefinition = "uuid")
    private UUID tagId;
}
