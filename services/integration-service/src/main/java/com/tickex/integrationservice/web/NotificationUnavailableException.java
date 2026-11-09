package com.tickex.integrationservice.web;

public class NotificationUnavailableException extends RuntimeException {

    public NotificationUnavailableException() {
        super("Notification service returned an empty response");
    }

    public NotificationUnavailableException(Throwable cause) {
        super("Notification service is unavailable", cause);
    }
}
