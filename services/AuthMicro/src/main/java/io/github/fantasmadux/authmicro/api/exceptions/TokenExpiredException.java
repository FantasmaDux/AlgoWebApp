package io.github.fantasmadux.authmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends HttpStatusException{
    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}