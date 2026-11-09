package com.tickex.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_mail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMailEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;  // relation YOK, düz UUID

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;
}
