package org.example.store.entities;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "solution",
        indexes = @Index(name = "solution_user_task_idx", columnList = "user_id, task_id"))
public class SolutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    @Column(nullable = false, length = 32)
    private String status;

    @Builder.Default
    @Column(name = "submitted_at", nullable = false)
    private Timestamp submittedAt = Timestamp.from(Instant.now());
}