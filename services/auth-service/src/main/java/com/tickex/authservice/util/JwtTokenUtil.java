package com.tickex.authservice.util;

import com.tickex.authservice.model.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenUtil {

    private final String issuer;
    private final Key accessKey;
    private final Key refreshKey;
    private final long accessTokenMinutes;
    private final long refreshTokenDays;

    public JwtTokenUtil(
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token.secret}") String accessSecret,
            @Value("${jwt.access-token.expiration-minutes}") long accessTokenMinutes,
            @Value("${jwt.refresh-token.secret}") String refreshSecret,
            @Value("${jwt.refresh-token.expiration-days}") long refreshTokenDays
    ) {
        this.issuer = issuer;
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String generateAccessToken(UUID userId, Set<Role> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);

        String rolesStr = roles.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .claim("roles", rolesStr)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenDays, ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(refreshKey)
                .compact();
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenMinutes * 60;
    }

    public long getRefreshTokenExpiresInSeconds() {
        return refreshTokenDays * 24 * 60 * 60;
    }
}
