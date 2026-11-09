package com.tickex.authservice.repository;

import com.tickex.authservice.model.entity.UserRetailProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRetailProfileRepository extends JpaRepository<UserRetailProfileEntity, UUID> {

    Optional<UserRetailProfileEntity> findByUserId(UUID userId);
}
