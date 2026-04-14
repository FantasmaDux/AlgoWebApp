package io.github.fantasmadux.authmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class CodeExpiredException extends AbstractException {
    public CodeExpiredException() {
        super("error.code.expired", "code.confirmation.error", HttpStatus.BAD_REQUEST, "code");
    }
}
