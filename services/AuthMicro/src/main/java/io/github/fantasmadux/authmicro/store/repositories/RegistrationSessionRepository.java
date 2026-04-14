package io.github.fantasmadux.authmicro.store.repositories;

import io.github.fantasmadux.authmicro.store.entities.RegistrationSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationSessionRepository extends JpaRepository<RegistrationSessionEntity, UUID> {
    Optional<RegistrationSessionEntity> findByEmail(String email);

    void deleteByEmail(String email);

    void deleteAllByCodeExpiresBefore(Timestamp fiveMinutesAgo);
}
