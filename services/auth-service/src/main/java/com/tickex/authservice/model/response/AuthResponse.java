package com.tickex.authservice.model.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    long accessTokenExpiresIn;
    long refreshTokenExpiresIn;
    UserSummaryResponse user;
}
