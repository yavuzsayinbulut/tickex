package com.tickex.authservice.repository;

import com.tickex.authservice.model.entity.UserLoginFailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface UserLoginFailRepository extends JpaRepository<UserLoginFailEntity, UUID> {

    long countByEmailIgnoreCaseAndCreatedAtAfter(String email, Instant createdAt);

    /**
     * Verilen tarihten daha eski tüm login fail kayıtlarını siler.
     */
    long deleteByCreatedAtBefore(Instant cutoff);
}
