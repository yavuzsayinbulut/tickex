package com.tickex.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_phone")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPhoneEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;  // relation yok

    @Column(name = "country_code", length = 8)
    private String countryCode; // örn. +90

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;
}
