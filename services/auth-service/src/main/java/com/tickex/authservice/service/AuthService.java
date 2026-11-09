package com.tickex.authservice.service;

import com.tickex.authservice.constant.ErrorCodes;
import com.tickex.authservice.exception.BusinessException;
import com.tickex.authservice.mapper.UserMapper;
import com.tickex.authservice.model.entity.UserLoginFailEntity;
import com.tickex.authservice.model.entity.UserMailEntity;
import com.tickex.authservice.model.entity.UserMasterEntity;
import com.tickex.authservice.model.entity.UserPhoneEntity;
import com.tickex.authservice.model.entity.UserRetailProfileEntity;
import com.tickex.authservice.model.entity.UserRoleEntity;
import com.tickex.authservice.model.enums.Role;
import com.tickex.authservice.model.enums.UserStatus;
import com.tickex.authservice.model.request.LoginRequest;
import com.tickex.authservice.model.request.RegisterRequest;
import com.tickex.authservice.model.response.AuthResponse;
import com.tickex.authservice.model.response.UserSummaryResponse;
import com.tickex.authservice.repository.UserLoginFailRepository;
import com.tickex.authservice.repository.UserMailRepository;
import com.tickex.authservice.repository.UserMasterRepository;
import com.tickex.authservice.repository.UserPhoneRepository;
import com.tickex.authservice.repository.UserRetailProfileRepository;
import com.tickex.authservice.repository.UserRoleRepository;
import com.tickex.authservice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    // -------------------------------------------------
    // Throttle config
    // -------------------------------------------------
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int FAILED_ATTEMPTS_WINDOW_MINUTES = 15;

    // -------------------------------------------------
    // Dependencies
    // -------------------------------------------------
    private final UserMasterRepository userMasterRepository;
    private final UserMailRepository userMailRepository;
    private final UserPhoneRepository userPhoneRepository;
    private final UserRetailProfileRepository userRetailProfileRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserLoginFailRepository userLoginFailRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    // =================================================
    // REGISTER
    // =================================================

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1) Email benzersizlik kontrolü
        userMailRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(mail -> {
                    throw new BusinessException(
                            ErrorCodes.EMAIL_ALREADY_IN_USE,
                            "Email already in use"
                    );
                });

        // 2) UserMaster (mapper)
        UserMasterEntity master = UserMapper.toUserMaster(request);
        userMasterRepository.save(master);
        UUID userId = master.getId();

        // 3) UserMail (mapper)
        String passwordHash = passwordEncoder.encode(request.getPassword());
        UserMailEntity mail = UserMapper.toUserMail(request, userId, passwordHash);
        userMailRepository.save(mail);

        // 4) Retail profil (mapper)
        UserRetailProfileEntity profile = UserMapper.toRetailProfile(request, userId);
        userRetailProfileRepository.save(profile);

        // 5) Telefon (varsa) (mapper)
        UserPhoneEntity phone = UserMapper.toUserPhone(request, userId);
        if (phone != null) {
            userPhoneRepository.save(phone);
        }

        // 6) Rol (default USER) (mapper)
        UserRoleEntity role = UserMapper.toDefaultUserRole(userId);
        userRoleRepository.save(role);

        // 7) Token + response
        return buildAuthResponse(master, mail);
    }

    // =================================================
    // LOGIN
    // =================================================

    // BURASI ÖNEMLİ: BusinessException için rollback yapılmıyor
    @Transactional(noRollbackFor = BusinessException.class)
    public AuthResponse login(LoginRequest request) {

        // 0) Throttle kontrolü - çok sık deneme var mı?
        Instant now = Instant.now();
        Instant windowStart = now.minus(FAILED_ATTEMPTS_WINDOW_MINUTES, ChronoUnit.MINUTES);

        long recentFails = userLoginFailRepository
                .countByEmailIgnoreCaseAndCreatedAtAfter(request.getEmail(), windowStart);

        if (recentFails >= MAX_FAILED_ATTEMPTS) {
            logLoginFail(null, request.getEmail(), "TOO_MANY_ATTEMPTS");
            throw new BusinessException(
                    ErrorCodes.INVALID_CREDENTIALS,
                    "Invalid credentials"
            );
        }

        // 1) Email üzerinden user_mail kaydını bul
        UserMailEntity mail = userMailRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> {
                    logLoginFail(null, request.getEmail(), "USER_NOT_FOUND");
                    return new BusinessException(
                            ErrorCodes.INVALID_CREDENTIALS,
                            "Invalid credentials"
                    );
                });

        // 2) Şifre kontrolü
        if (!passwordEncoder.matches(request.getPassword(), mail.getPasswordHash())) {
            logLoginFail(mail.getUserId(), mail.getEmail(), "INVALID_PASSWORD");
            throw new BusinessException(
                    ErrorCodes.INVALID_CREDENTIALS,
                    "Invalid credentials"
            );
        }

        // 3) User master
        UserMasterEntity master = userMasterRepository.findById(mail.getUserId())
                .orElseThrow(() -> {
                    logLoginFail(mail.getUserId(), mail.getEmail(), "MASTER_NOT_FOUND");
                    return new BusinessException(
                            ErrorCodes.INVALID_CREDENTIALS,
                            "Invalid credentials"
                    );
                });

        // 4) Kullanıcı statüsü kontrolü
        if (master.getStatus() != UserStatus.ACTIVE) {
            logLoginFail(master.getId(), mail.getEmail(),
                    "USER_STATUS_" + master.getStatus().name());
            throw new BusinessException(
                    ErrorCodes.INVALID_CREDENTIALS,
                    "Invalid credentials"
            );
        }

        // 5) Başarılı login → token + user summary
        return buildAuthResponse(master, mail);
    }

    // =================================================
    // PRIVATE HELPERS
    // =================================================

    private void logLoginFail(UUID userId, String email, String reason) {
        UserLoginFailEntity fail = UserLoginFailEntity.builder()
                .userId(userId)
                .email(email)
                .reason(reason)
                .build();
        userLoginFailRepository.save(fail);
    }

    private AuthResponse buildAuthResponse(UserMasterEntity master, UserMailEntity mail) {
        UUID userId = master.getId();

        // Roller
        List<UserRoleEntity> roleEntities = userRoleRepository.findByUserId(userId);
        Set<Role> roles = roleEntities.stream()
                .map(UserRoleEntity::getRole)
                .collect(Collectors.toSet());

        // Retail profil (opsiyonel)
        UserRetailProfileEntity profile = userRetailProfileRepository.findByUserId(userId)
                .orElse(null);

        // Telefon (primary, opsiyonel)
        UserPhoneEntity phone = userPhoneRepository.findByUserIdAndPrimaryTrue(userId)
                .orElse(null);

        // JWT üretimi
        String accessToken = jwtTokenUtil.generateAccessToken(userId, roles);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userId);

        // User summary mapper ile
        UserSummaryResponse userSummary =
                UserMapper.toUserSummary(master, mail, profile, phone, roles);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(jwtTokenUtil.getAccessTokenExpiresInSeconds())
                .refreshTokenExpiresIn(jwtTokenUtil.getRefreshTokenExpiresInSeconds())
                .user(userSummary)
                .build();
    }
}
