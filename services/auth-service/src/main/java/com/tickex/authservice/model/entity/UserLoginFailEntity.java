package com.tickex.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_login_fail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginFailEntity extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;  // her zaman dolu olmak zorunda değil

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "reason", length = 255)
    private String reason;
}
