package com.tickex.authservice.model.response;

import com.tickex.authservice.model.enums.Role;
import com.tickex.authservice.model.enums.UserStatus;
import com.tickex.authservice.model.enums.UserType;
import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class UserSummaryResponse {
    UUID id;
    String email;
    String firstName;
    String lastName;
    String phoneCountryCode;
    String phoneNumber;
    Set<Role> roles;
    UserStatus status;
    UserType userType;
}
