package com.tickex.authservice.repository;

import com.tickex.authservice.model.entity.UserMailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserMailRepository extends JpaRepository<UserMailEntity, UUID> {

    Optional<UserMailEntity> findByEmailIgnoreCase(String email);

    Optional<UserMailEntity> findByUserIdAndPrimaryTrue(UUID userId);
}
