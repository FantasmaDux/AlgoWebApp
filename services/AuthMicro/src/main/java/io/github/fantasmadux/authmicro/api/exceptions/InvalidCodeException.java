package io.github.fantasmadux.authmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCodeException extends AbstractException {

    public InvalidCodeException() {
        super("error.invalid.code", "code.confirmation.error", HttpStatus.BAD_REQUEST, "code");
    }
}

