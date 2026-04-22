package org.example.validators;

import io.github.fantasmadux.authmicro.api.dto.FieldErrorDto;
import io.github.fantasmadux.authmicro.api.exceptions.CodeExpiredException;
import io.github.fantasmadux.authmicro.api.exceptions.FieldValidationException;
import io.github.fantasmadux.authmicro.api.exceptions.InvalidCodeException;
import io.github.fantasmadux.authmicro.store.entities.RegistrationSessionEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegistrationValidation {
    private final PasswordEncoder passwordEncoder;

    public boolean isCodeExpired(RegistrationSessionEntity session) {
        if (session.getCodeExpires().after(new Timestamp(System.currentTimeMillis()))) {
            return false;
        }
        return true;
    }

    public void ensureCodeIsNotExpired(RegistrationSessionEntity session) {
        if (session.getCodeExpires().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CodeExpiredException();
        }
    }

    public void checkIfCodeIsValid(String code, RegistrationSessionEntity registrationSession) {
        if (code == null || code.isBlank() || !passwordEncoder.matches(code, registrationSession.getCode())) {
            throw new InvalidCodeException();
        }
    }

    public String getTrimmedEmailOrThrow(String email) {
        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        if (email == null || email.trim().isEmpty()) {
            fieldErrors.add(
                    new FieldErrorDto("email", "field.empty")
            );
            throw new FieldValidationException("registration.error", fieldErrors);
        }
        return email.trim();
    }


    public String getTrimmedCodeOrThrow(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidCodeException();
        }
        return code.trim();
    }

    public void validateEmailFormatOrThrow(String email) {
        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        EmailValidator validator = EmailValidator.getInstance(false, true);

        if (email == null || !validator.isValid(email)) {
            fieldErrors.add(
                    new FieldErrorDto("email", "email.format.incorrect")
            );
            throw new FieldValidationException("registration.error", fieldErrors);
        }
    }

    public void validateUserData(Map<String, Object> userData) {
        List<FieldErrorDto> fieldErrors = new ArrayList<>();

        // Валидация email
        String email = (String) userData.get("email");
        if (email == null || email.trim().isEmpty()) {
            fieldErrors.add(new FieldErrorDto("email", "field.empty"));
        } else {
            email = email.trim();
            EmailValidator validator = EmailValidator.getInstance(false, true);
            if (!validator.isValid(email)) {
                fieldErrors.add(new FieldErrorDto("email", "email.format.incorrect"));
            }
        }


        // Если есть ошибки, бросаем исключение
        if (!fieldErrors.isEmpty()) {
            throw new FieldValidationException("registration.error", fieldErrors);
        }
    }
}
