package com.project.api.exception;

import org.springframework.http.HttpStatus;

public class GroupMatchingFailException extends RuntimeException{
    private final HttpStatus status;

    public GroupMatchingFailException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
