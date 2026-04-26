package org.example.store.repositories;

import org.example.store.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
    List<TestEntity> findByTaskId(Long taskId);
}