package com.tickex.authservice.repository;

import com.tickex.authservice.model.entity.UserPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPhoneRepository extends JpaRepository<UserPhoneEntity, UUID> {

    Optional<UserPhoneEntity> findByUserIdAndPrimaryTrue(UUID userId);
}
