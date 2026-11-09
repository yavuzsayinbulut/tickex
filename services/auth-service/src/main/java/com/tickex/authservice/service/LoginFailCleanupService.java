package com.tickex.authservice.service;

import com.tickex.authservice.repository.UserLoginFailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginFailCleanupService {

    private final UserLoginFailRepository userLoginFailRepository;

    /**
     * Kayıtların tutulacağı süre (gün cinsinden).
     * Örn. application.yml içinde:
     *
     * auth:
     *   login-fail:
     *     retention-days: 30
     */
    @Value("${auth.login-fail.retention-days:30}")
    private long retentionDays;

    /**
     * Her gece 03:00'te (server saati) çalışır ve retention süresinden eski
     * user_login_fail kayıtlarını siler.
     *
     * Cron: saniye dakika saat gün ay haftaGünü
     * "0 0 3 * * *" = 03:00
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldLoginFailRecords() {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        long deleted = userLoginFailRepository.deleteByCreatedAtBefore(cutoff);

        if (deleted > 0) {
            log.info("LoginFailCleanupService: {} login fail kaydı silindi. cutoff={}", deleted, cutoff);
        } else {
            log.debug("LoginFailCleanupService: silinecek eski login fail kaydı yok. cutoff={}", cutoff);
        }
    }
}
