package io.github.fantasmadux.authmicro.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fantasmadux.authmicro.api.dto.responses.RegistrationResponseDto;
import io.github.fantasmadux.authmicro.api.exceptions.InvalidCodeException;
import io.github.fantasmadux.authmicro.api.exceptions.ServerAnswerException;
import io.github.fantasmadux.authmicro.components.CodeGenerator;
import io.github.fantasmadux.authmicro.store.entities.MailEntity;
import io.github.fantasmadux.authmicro.store.entities.RegistrationSessionEntity;
import io.github.fantasmadux.authmicro.store.entities.UserEntity;
import io.github.fantasmadux.authmicro.store.repositories.RegistrationSessionRepository;
import io.github.fantasmadux.authmicro.store.repositories.UserRepository;
import io.github.fantasmadux.authmicro.validators.RegistrationValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationSessionRepository registrationSessionRepository;
    private final CodeGenerator codeGenerator;
    private final RegistrationValidation registrationValidator;
    private final SessionCleanerService sessionCleanerService;
    private final EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final UserRepository userRepository;

    public RegistrationResponseDto register(JsonNode registrationRequest) {

        String email = registrationRequest.path("email").asText(null);

        Map<String, Object> userData = convertJsonNodeToMap(registrationRequest);


        registrationValidator.validateUserData(userData);

        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Пользователь существует -> создаем фейковую сессию
            return fakeRegistrationSessionCreateAndSave(email);
        }

        // Пользователь не существует
        Optional<RegistrationSessionEntity> sessionOpt = registrationSessionRepository.findByEmail(email);

        if (sessionOpt.isPresent()) {
            return handleExistingRegistrationSession(sessionOpt.get());
        } else {
            return handleNewRegistrationSession(email);
        }
    }


    @Transactional
    public void confirmEmail(JsonNode registrationConfirmRequest, String ip) {

        String email = registrationConfirmRequest.path("email").asText(null);
        String code = registrationConfirmRequest.path("code").asText(null);

        code = registrationValidator.getTrimmedCodeOrThrow(code);

        Map<String, Object> userData = convertJsonNodeToMap(registrationConfirmRequest);

        registrationValidator.validateUserData(userData);

        email = email.trim();

        RegistrationSessionEntity registrationSession = registrationSessionRepository
                .findByEmail(email)
                .orElse(null);

        if (registrationSession == null) {
            throw new InvalidCodeException();
        }

        registrationValidator.checkIfCodeIsValid(code, registrationSession);

        registrationValidator.ensureCodeIsNotExpired(registrationSession);

        UserEntity newUser = UserEntity.builder()
                .email(email)
                .build();

        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            throw new ServerAnswerException();
        }


        sessionCleanerService.cleanRegistrationSession(registrationSession);
    }


    private Map<String, Object> convertJsonNodeToMap(JsonNode registrationRequest) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(registrationRequest, new TypeReference<>() {
        });
    }

    private RegistrationResponseDto fakeRegistrationSessionCreateAndSave(String email) {

        RegistrationSessionEntity fakeSession = registrationSessionRepository
                .findByEmail(email)
                .orElseGet(() -> {
                    RegistrationSessionEntity newSession = new RegistrationSessionEntity();
                    newSession.setEmail(email);
                    return newSession;
                });

        Timestamp currentExpiresTime = fakeSession.getCodeExpires();

        if (currentExpiresTime != null &&
                currentExpiresTime.after(new Timestamp(System.currentTimeMillis()))) {
            return new RegistrationResponseDto(currentExpiresTime.getTime(), codeGenerator.getCodePattern());
        }

        long fakeCodeExpires = codeGenerator.codeExpiresGenerate();

        fakeSession.setEmail(email);
        fakeSession.setCode("");
        fakeSession.setCodeExpires(new Timestamp(fakeCodeExpires));

        log.info("FAKE_REGISTRATION_CODE email={} code={}", email, "");

        registrationSessionRepository.save(fakeSession);

        return new RegistrationResponseDto(fakeCodeExpires, codeGenerator.getCodePattern());
    }


    private RegistrationResponseDto handleExistingRegistrationSession(RegistrationSessionEntity registrationSession) {

        String rawCode = codeGenerator.codeGenerate();
        long codeExpires = codeGenerator.codeExpiresGenerate();
        Timestamp codeExpiresAt = new Timestamp(codeExpires);

        boolean isFake = registrationSession.getCode().isEmpty();
        boolean isExpired = registrationValidator.isCodeExpired(registrationSession);

        if (isFake || isExpired) {
            return refreshCodeAndReturnRegistrationResponseDto(
                    registrationSession,
                    rawCode, codeExpiresAt
            );
        }

        return new RegistrationResponseDto(
                registrationSession.getCodeExpires().getTime(),
                codeGenerator.getCodePattern()
        );
    }

    private RegistrationResponseDto refreshCodeAndReturnRegistrationResponseDto(RegistrationSessionEntity registrationSession, String code, Timestamp codeExpires) {
        String hashedCode = codeGenerator.codeHash(code);
        registrationSession.setCode(hashedCode);
        registrationSession.setCodeExpires(codeExpires);
        registrationSessionRepository.save(registrationSession);
        log.info("REGISTRATION_CODE email={} code={}", registrationSession.getEmail(), code);

        emailService.sendEmailForRegistration(
                MailEntity.builder()
                        .receiver(registrationSession.getEmail())
                        .body("Ваш код: " + code)
                        .build()
        );

        return new RegistrationResponseDto(codeExpires.getTime(),
                codeGenerator.getCodePattern());
    }

    private RegistrationResponseDto handleNewRegistrationSession(String email) {
        String rawCode = codeGenerator.codeGenerate();
        String hashedCode = codeGenerator.codeHash(rawCode);
        long codeExpires = codeGenerator.codeExpiresGenerate();
        log.info("REGISTRATION_CODE email={} code={}", email, rawCode);

        emailService.sendEmailForRegistration(
                MailEntity.builder()
                        .receiver(email)
                        .body("Ваш код: " + rawCode)
                        .build()
        );


        return returnNewRegistrationResponseDto(email, hashedCode, new Timestamp(codeExpires));
    }

    private RegistrationResponseDto returnNewRegistrationResponseDto(String email, String code, Timestamp codeExpires) {
        RegistrationSessionEntity registrationSession =
                registrationSessionRepository.save(
                        RegistrationSessionEntity.builder()
                                .email(email)
                                .code(code)
                                .codeExpires(codeExpires)
                                .build()
                );

        return new RegistrationResponseDto(
                registrationSession.getCodeExpires().getTime(),
                codeGenerator.getCodePattern()
        );
    }
}