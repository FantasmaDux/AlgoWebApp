package org.example.api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends HttpStatusException {
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
