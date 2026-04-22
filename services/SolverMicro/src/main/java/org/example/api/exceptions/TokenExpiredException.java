package org.example.api.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends HttpStatusException{
    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}