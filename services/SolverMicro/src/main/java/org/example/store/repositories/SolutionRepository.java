package org.example.store.repositories;

import org.example.store.entities.SolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<SolutionEntity, Long> {
    Optional<SolutionEntity> findByTaskIdAndUserId(Long taskId, UUID userId);

    List<SolutionEntity> findByUserId(UUID userId);

    boolean existsByTaskIdAndUserIdAndStatus(Long taskId, UUID userId, String status);

    List<SolutionEntity> findByTaskIdAndStatus(Long taskId, String status);
}