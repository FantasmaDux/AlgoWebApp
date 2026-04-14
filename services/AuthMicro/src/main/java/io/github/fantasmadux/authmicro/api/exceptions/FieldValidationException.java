package io.github.fantasmadux.authmicro.api.exceptions;

import io.github.fantasmadux.authmicro.api.dto.FieldErrorDto;
import lombok.Getter;

import java.util.List;

@Getter
public class FieldValidationException extends RuntimeException {
    private final List<FieldErrorDto> errors;
    private final String message;

    public FieldValidationException(String message, List<FieldErrorDto> errors) {
        this.message = message;
        this.errors = errors;
    }

}
