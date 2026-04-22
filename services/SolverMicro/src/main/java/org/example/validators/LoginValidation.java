package org.example.validators;

import io.github.fantasmadux.authmicro.api.dto.FieldErrorDto;
import io.github.fantasmadux.authmicro.api.exceptions.CodeExpiredException;
import io.github.fantasmadux.authmicro.api.exceptions.FieldValidationException;
import io.github.fantasmadux.authmicro.api.exceptions.InvalidCodeException;
import io.github.fantasmadux.authmicro.store.entities.LoginSessionEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LoginValidation {

    private final PasswordEncoder passwordEncoder;

    public String getTrimmedEmailOrThrow(String email) {
        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        if (email == null || email.trim().isEmpty()) {
            fieldErrors.add(
                    new FieldErrorDto("email", "field.empty")
            );
            throw new FieldValidationException("login.error", fieldErrors);
        }
        return email.trim();
    }

    public void checkIfCodeIsValid(LoginSessionEntity session, String code) {
        if (code == null || code.isBlank() || !passwordEncoder.matches(code, session.getCode())) {
            throw new InvalidCodeException();
        }
    }

    public void ensureCodeIsNotExpired(LoginSessionEntity session) {
        if (session.getCodeExpires().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CodeExpiredException();
        }
    }

    public LoginSessionEntity validateLoginSessionOrThrow(Optional<LoginSessionEntity> loginSessionOpt) {
        if (loginSessionOpt.isEmpty()) {
            throw new InvalidCodeException();
        }
        LoginSessionEntity loginSession = loginSessionOpt.get();

        if (loginSession.getAccountId() == null) {
            throw new InvalidCodeException();
        }
        return loginSession;
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
}
