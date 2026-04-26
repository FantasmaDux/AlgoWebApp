package org.example.store.repositories;

import org.example.store.entities.PatternEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternRepository extends JpaRepository<PatternEntity, Long> {
}