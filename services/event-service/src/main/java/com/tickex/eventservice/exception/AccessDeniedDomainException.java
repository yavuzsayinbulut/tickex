package com.tickex.eventservice.exception;

public class AccessDeniedDomainException extends RuntimeException {

    public AccessDeniedDomainException(String message) {
        super(message);
    }
}
