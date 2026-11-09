package com.tickex.ticketingservice.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public ApiException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }

    public static ApiException badRequest(String code, String msg) {
        return new ApiException(code, HttpStatus.BAD_REQUEST, msg);
    }

    public static ApiException notFound(String code, String msg) {
        return new ApiException(code, HttpStatus.NOT_FOUND, msg);
    }
}
