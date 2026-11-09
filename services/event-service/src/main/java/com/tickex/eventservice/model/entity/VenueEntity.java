package com.tickex.eventservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "venue")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;
}
