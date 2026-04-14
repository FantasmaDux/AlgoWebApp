package io.github.fantasmadux.authmicro.services;


import io.github.fantasmadux.authmicro.api.dto.responses.LoginConfirmResponseDto;
import io.github.fantasmadux.authmicro.api.dto.responses.LoginResponseDto;
import io.github.fantasmadux.authmicro.api.exceptions.InvalidCodeException;
import io.github.fantasmadux.authmicro.components.CodeGenerator;
import io.github.fantasmadux.authmicro.store.entities.LoginSessionEntity;
import io.github.fantasmadux.authmicro.store.entities.MailEntity;
import io.github.fantasmadux.authmicro.store.entities.RefreshTokenSessionEntity;
import io.github.fantasmadux.authmicro.store.entities.UserEntity;
import io.github.fantasmadux.authmicro.store.repositories.LoginSessionRepository;
import io.github.fantasmadux.authmicro.store.repositories.RefreshTokenSessionRepository;
import io.github.fantasmadux.authmicro.store.repositories.UserRepository;
import io.github.fantasmadux.authmicro.util.JwtUtil;
import io.github.fantasmadux.authmicro.validators.LoginValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginSessionRepository loginSessionRepository;
    private final CodeGenerator codeGenerator;
    private final JwtUtil jwtUtil;
    private final LoginValidation loginValidator;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final SessionCleanerService sessionCleanerService;
    private final EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final UserRepository userRepository;

    public LoginResponseDto login(String email) {

        email = loginValidator.getTrimmedEmailOrThrow(email);
        loginValidator.validateEmailFormatOrThrow(email);

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        LoginResponseDto loginResponse;

        if (userOpt.isEmpty()) {
            loginResponse = fakeLoginSessionCreateAndSave(email);
        } else {
            UUID accountId = userOpt.get().getId();

            Optional<LoginSessionEntity> sessionOpt = loginSessionRepository.findByEmail(email);

            if (sessionOpt.isPresent()) {
                loginResponse = handleExistingLoginSession(sessionOpt.get(), email, accountId);
            } else {
                loginResponse = handleNewLoginSession(email, accountId);
            }
        }

        return loginResponse;

    }

    @Transactional
    public LoginConfirmResponseDto confirmLoginEmail(String email, String code, String ip, String userAgent) {
        email = loginValidator.getTrimmedEmailOrThrow(email);
        loginValidator.validateEmailFormatOrThrow(email);
        code = loginValidator.getTrimmedCodeOrThrow(code);

        Optional<LoginSessionEntity> loginSessionOpt = loginSessionRepository.findByEmail(email);

        LoginSessionEntity loginSession = loginValidator.validateLoginSessionOrThrow(loginSessionOpt);

        loginValidator.checkIfCodeIsValid(loginSession, code);
        loginValidator.ensureCodeIsNotExpired(loginSession);

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            sessionCleanerService.cleanLoginSession(loginSession);
            throw new InvalidCodeException();
        }

        UserEntity user = userOpt.get();
        UUID accountId = user.getId();

        if (!accountId.equals(loginSession.getAccountId())) {
            throw new InvalidCodeException();
        }

        Map<String, Object> tokens = generateTokens(accountId, false);


        refreshTokenSessionCreateAndSave(ip, userAgent, accountId,
                (String) tokens.get("refreshToken"),
                new Timestamp((Long) tokens.get("refreshTokenExpires")));


        sessionCleanerService.cleanLoginSession(loginSession);

        return loginConfirmResponseBuild(tokens);
    }

    private LoginResponseDto handleExistingLoginSession(LoginSessionEntity session,
                                                        String email, UUID accountId) {

        boolean isFake = session.getCode().isEmpty();
        boolean isExpired = session.getCodeExpires().before(Timestamp.from(Instant.now()));

        if (isFake || isExpired || session.getAccountId() == null || !session.getAccountId().equals(accountId)) {
            String rawRefreshCode = codeGenerator.codeGenerate();
            String hashedRefreshCode = codeGenerator.codeHash(rawRefreshCode);
            long refreshCodeExpires = codeGenerator.codeExpiresGenerate();

            session.setAccountId(accountId);
            session.setCode(hashedRefreshCode);
            session.setCodeExpires(new Timestamp(refreshCodeExpires));

            log.info("LOGIN_CODE email={} code={}", email, rawRefreshCode);
            emailService.sendEmailForLogin(
                    MailEntity.builder()
                            .receiver(session.getEmail())
                            .body("Ваш код: " + rawRefreshCode)
                            .build()
            );
            loginSessionRepository.save(session);

            return new LoginResponseDto(refreshCodeExpires, codeGenerator.getCodePattern());
        }
        return new LoginResponseDto(session.getCodeExpires().getTime(), codeGenerator.getCodePattern());
    }

    private LoginResponseDto handleNewLoginSession(String email, UUID accountId) {

        String rawCode = codeGenerator.codeGenerate();
        String hashedCode = codeGenerator.codeHash(rawCode);
        long codeExpires = codeGenerator.codeExpiresGenerate();

        LoginSessionEntity loginSession = LoginSessionEntity.builder()
                .accountId(accountId)
                .email(email)
                .code(hashedCode)
                .codeExpires(new Timestamp(codeExpires))
                .build();
        log.info("LOGIN_CODE email={} code={}", email, rawCode);
        emailService.sendEmailForLogin(
                MailEntity.builder()
                        .receiver(email)
                        .body("Ваш код: " + rawCode)
                        .build()
        );
        loginSessionRepository.save(loginSession);

        return new LoginResponseDto(codeExpires, codeGenerator.getCodePattern());
    }

    private static LoginConfirmResponseDto loginConfirmResponseBuild(Map<String, Object> tokens) {
        return LoginConfirmResponseDto.builder()
                .accessToken((String) tokens.get("accessToken"))
                .refreshToken((String) tokens.get("refreshToken"))
                .accessTokenExpires((long) tokens.get("accessTokenExpires"))
                .refreshTokenExpires((long) tokens.get("refreshTokenExpires"))
                .build();
    }

    private Map<String, Object> generateTokens(UUID accountId, boolean isAdmin) {
        String accessToken = jwtUtil.generateAccessToken(accountId, isAdmin);
        String refreshToken = jwtUtil.generateRefreshToken(accountId);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("accessTokenExpires", jwtUtil.extractExpiration(accessToken).getTime());
        tokens.put("refreshTokenExpires", jwtUtil.extractExpiration(refreshToken).getTime());
        return tokens;
    }

    private void refreshTokenSessionCreateAndSave(String ip, String userAgent, UUID accountId, String refreshToken, Timestamp refreshTokenExpires) {
        RefreshTokenSessionEntity refreshTokenSession = RefreshTokenSessionEntity.builder()
                .accountId(accountId)
                .refreshToken(refreshToken)
                .userAgent(userAgent)
                .ip(ip)
                .expiresAt(refreshTokenExpires)
                .build();

        refreshTokenSessionRepository.save(refreshTokenSession);
    }

    private LoginResponseDto fakeLoginSessionCreateAndSave(String email) {

        LoginSessionEntity fakeSession = loginSessionRepository
                .findByEmail(email)
                .orElseGet(() -> {
                    LoginSessionEntity newSession = new LoginSessionEntity();
                    newSession.setEmail(email);
                    return newSession;
                });

        Timestamp currentExpiresTime = fakeSession.getCodeExpires();

        if (currentExpiresTime != null &&
                currentExpiresTime.after(new Timestamp(System.currentTimeMillis()))) {
            return new LoginResponseDto(currentExpiresTime.getTime(), codeGenerator.getCodePattern());
        }

        long fakeCodeExpires = codeGenerator.codeExpiresGenerate();

        fakeSession.setAccountId(null);
        fakeSession.setEmail(email);
        fakeSession.setCode("");
        fakeSession.setCodeExpires(new Timestamp(fakeCodeExpires));

        log.info("FAKE_LOGIN_CODE email={} code={}", email, "");

        loginSessionRepository.save(fakeSession);

        return new LoginResponseDto(fakeCodeExpires, codeGenerator.getCodePattern());
    }
}