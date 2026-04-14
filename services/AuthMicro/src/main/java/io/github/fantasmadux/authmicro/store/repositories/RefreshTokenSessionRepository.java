package io.github.fantasmadux.authmicro.store.repositories;

import io.github.fantasmadux.authmicro.store.entities.RefreshTokenSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSessionEntity, UUID> {
    boolean existsByRefreshToken(String refreshToken);

    Optional<RefreshTokenSessionEntity> findByRefreshToken(String refreshToken);

    void deleteAllByExpiresAtBefore(Timestamp now);
}
