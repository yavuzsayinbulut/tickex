package com.tickex.integrationservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IntegrationExceptionHandler {

    @ExceptionHandler(NotificationUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("NOTIFICATION_UNAVAILABLE", "Notification service is unavailable"));
    }

    public record ErrorResponse(String code, String message) {
    }
}
