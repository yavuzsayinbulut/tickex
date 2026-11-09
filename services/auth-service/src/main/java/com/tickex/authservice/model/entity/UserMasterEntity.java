package com.tickex.authservice.model.entity;

import com.tickex.authservice.model.enums.UserStatus;
import com.tickex.authservice.model.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMasterEntity extends BaseEntity {

    @Column(name = "identity_hash", length = 255)
    private String identityHash;   // TCKN vb. hashlenmiş değer

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 50)
    private UserType userType;
}
