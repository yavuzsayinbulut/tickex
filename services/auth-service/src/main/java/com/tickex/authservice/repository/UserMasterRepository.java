package com.tickex.authservice.repository;

import com.tickex.authservice.model.entity.UserMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserMasterRepository extends JpaRepository<UserMasterEntity, UUID> {
}
