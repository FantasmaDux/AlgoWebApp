package io.github.fantasmadux.authmicro.services;

import io.github.fantasmadux.authmicro.api.dto.responses.RefreshTokenResponseDto;
import io.github.fantasmadux.authmicro.api.exceptions.InvalidTokenException;
import io.github.fantasmadux.authmicro.store.entities.RefreshTokenSessionEntity;
import io.github.fantasmadux.authmicro.store.entities.UserEntity;
import io.github.fantasmadux.authmicro.store.repositories.RefreshTokenSessionRepository;
import io.github.fantasmadux.authmicro.store.repositories.UserRepository;
import io.github.fantasmadux.authmicro.util.JwtUtil;
import io.github.fantasmadux.authmicro.validators.RefreshTokenValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenValidation refreshTokenValidator;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final SessionCleanerService sessionCleanerService;
    private final UserRepository userRepository;

    @Transactional
    public RefreshTokenResponseDto refreshTokens(String refreshToken) {

        refreshTokenValidator.checkIfTokenExistsOrThrow(refreshToken);

        Jwt decodedToken = refreshTokenValidator.getDecodedTokenOrThrow(refreshToken);


        refreshTokenValidator.checkIfTokenValidOrThrow(decodedToken);
        refreshTokenValidator.checkIfTokenNotExpiredOrThrow(decodedToken);

        String accountIdStr = decodedToken.getClaimAsString("accountId");
        UUID accountId = UUID.fromString(accountIdStr);

        Optional<UserEntity> userOpt = userRepository.findById(accountId);
        if (userOpt.isEmpty()) {
            throw new InvalidTokenException();
        }

        String newAccessToken = jwtUtil.generateAccessToken(accountId, false);
        String newRefreshToken = jwtUtil.generateRefreshToken(accountId);

        Timestamp accessTokenExpires = jwtUtil.extractExpiration(newAccessToken);
        Timestamp refreshTokenExpires = jwtUtil.extractExpiration(newRefreshToken);

        RefreshTokenSessionEntity oldSession = refreshTokenSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException());

        RefreshTokenSessionEntity session = RefreshTokenSessionEntity.builder()
                .accountId(accountId)
                .refreshToken(newRefreshToken)
                .ip(oldSession.getIp())
                .userAgent(oldSession.getUserAgent())
                .expiresAt(refreshTokenExpires)
                .build();

        sessionCleanerService.cleanRefreshTokenSession(oldSession);

        refreshTokenSessionRepository.save(session);


        return new RefreshTokenResponseDto(newRefreshToken,
                refreshTokenExpires.getTime(), newAccessToken, accessTokenExpires.getTime());
    }
}
