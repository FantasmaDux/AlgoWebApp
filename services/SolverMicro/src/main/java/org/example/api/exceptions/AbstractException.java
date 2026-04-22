package org.example.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractException extends RuntimeException {
    private final String messageCode;
    private final String errorCode;
    private final HttpStatus status;
    private final String fieldName;

    public AbstractException(String messageCode, String errorCode, HttpStatus status, String fieldName) {
        this.errorCode = errorCode;
        this.status = status;
        this.messageCode = messageCode;
        this.fieldName = fieldName;
    }
}