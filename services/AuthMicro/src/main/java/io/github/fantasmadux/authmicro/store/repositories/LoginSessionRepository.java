package io.github.fantasmadux.authmicro.store.repositories;

import io.github.fantasmadux.authmicro.store.entities.LoginSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, UUID> {
    Optional<LoginSessionEntity> findByAccountIdAndEmail(UUID accountId, String email);

    Optional<LoginSessionEntity> findByEmail(String email);

    void deleteAllByCodeExpiresBefore(Timestamp fiveMinutesAgo);

    boolean existsByEmail(String mail);
}
