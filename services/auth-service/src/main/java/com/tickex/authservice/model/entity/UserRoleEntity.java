package com.tickex.authservice.model.entity;

import com.tickex.authservice.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;  // user_master.id

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;
}
