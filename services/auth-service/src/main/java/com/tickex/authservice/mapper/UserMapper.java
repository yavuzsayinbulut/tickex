package com.tickex.authservice.mapper;

import com.tickex.authservice.model.entity.UserMailEntity;
import com.tickex.authservice.model.entity.UserMasterEntity;
import com.tickex.authservice.model.entity.UserPhoneEntity;
import com.tickex.authservice.model.entity.UserRetailProfileEntity;
import com.tickex.authservice.model.entity.UserRoleEntity;
import com.tickex.authservice.model.enums.Role;
import com.tickex.authservice.model.enums.UserStatus;
import com.tickex.authservice.model.enums.UserType;
import com.tickex.authservice.model.request.RegisterRequest;
import com.tickex.authservice.model.response.UserSummaryResponse;
import com.tickex.authservice.util.HashUtil;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.UUID;

public final class UserMapper {

    private UserMapper() {
        // utility
    }

    // ----------------------------------------------------------------
    // REGISTER → ENTITY DÖNÜŞÜMLERİ
    // ----------------------------------------------------------------

    /**
     * RegisterRequest'ten user_master kaydını üretir.
     * Status: ACTIVE, UserType: RETAIL olarak set edilir.
     * Identity number varsa SHA-256 ile hashlenir.
     */
    public static UserMasterEntity toUserMaster(RegisterRequest request) {
        UserMasterEntity master = new UserMasterEntity();
        master.setStatus(UserStatus.ACTIVE);
        master.setUserType(UserType.RETAIL);

        if (StringUtils.hasText(request.getIdentityNumber())) {
            master.setIdentityHash(HashUtil.sha256(request.getIdentityNumber()));
        }

        return master;
    }

    /**
     * RegisterRequest + userId + passwordHash → user_mail kaydı.
     */
    public static UserMailEntity toUserMail(RegisterRequest request,
                                            UUID userId,
                                            String passwordHash) {

        return UserMailEntity.builder()
                .userId(userId)
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .primary(true)
                .verified(false)
                .build();
    }

    /**
     * RegisterRequest + userId → user_retail_profile kaydı.
     */
    public static UserRetailProfileEntity toRetailProfile(RegisterRequest request,
                                                          UUID userId) {

        return UserRetailProfileEntity.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .nationality(request.getNationality())
                .gender(request.getGender())
                .build();
    }

    /**
     * RegisterRequest + userId → user_phone kaydı.
     * Telefon numarası yoksa null döner, caller karar verir.
     */
    public static UserPhoneEntity toUserPhone(RegisterRequest request,
                                              UUID userId) {

        if (!StringUtils.hasText(request.getPhoneNumber())) {
            return null;
        }

        return UserPhoneEntity.builder()
                .userId(userId)
                .countryCode(request.getPhoneCountryCode())
                .phoneNumber(request.getPhoneNumber())
                .primary(true)
                .verified(false)
                .build();
    }

    /**
     * Yeni kayıt olan kullanıcı için default USER rolü.
     */
    public static UserRoleEntity toDefaultUserRole(UUID userId) {
        return UserRoleEntity.builder()
                .userId(userId)
                .role(Role.USER)
                .build();
    }

    // ----------------------------------------------------------------
    // ENTITY → RESPONSE DÖNÜŞÜMLERİ
    // ----------------------------------------------------------------

    /**
     * master + mail + profil + telefon + roller → UserSummaryResponse
     */
    public static UserSummaryResponse toUserSummary(UserMasterEntity master,
                                                    UserMailEntity mail,
                                                    UserRetailProfileEntity profile,
                                                    UserPhoneEntity phone,
                                                    Set<Role> roles) {

        String firstName = profile != null ? profile.getFirstName() : null;
        String lastName = profile != null ? profile.getLastName() : null;

        String phoneCountryCode = phone != null ? phone.getCountryCode() : null;
        String phoneNumber = phone != null ? phone.getPhoneNumber() : null;

        return UserSummaryResponse.builder()
                .id(master.getId())
                .email(mail.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .phoneCountryCode(phoneCountryCode)
                .phoneNumber(phoneNumber)
                .roles(roles)
                .status(master.getStatus())
                .userType(master.getUserType())
                .build();
    }
}
