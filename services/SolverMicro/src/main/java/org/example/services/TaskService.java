package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.responses.TaskResponseDto;
import org.example.store.entities.TaskEntity;
import org.example.store.repositories.PatternRepository;
import org.example.store.repositories.SolutionRepository;
import org.example.store.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final PatternRepository patternRepository;
    private final SolutionRepository solutionRepository;

    public TaskResponseDto getTaskById(Long taskId, UUID userId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        boolean isSolved = solutionRepository.existsByTaskIdAndUserIdAndStatus(
                taskId, userId, "SOLVED"
        );

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .number(task.getNumber())
                .patternId(task.getPattern().getId())
                .patternName(task.getPattern().getName())
                .isSolved(isSolved)
                .build();
    }

    public List<TaskResponseDto> getTasksByPatternId(Long patternId, UUID userId) {
        List<TaskEntity> tasks = taskRepository.findByPatternId(patternId);

        return tasks.stream()
                .map(task -> {
                    boolean isSolved = solutionRepository.existsByTaskIdAndUserIdAndStatus(
                            task.getId(), userId, "SOLVED"
                    );
                    return TaskResponseDto.builder()
                            .id(task.getId())
                            .name(task.getName())
                            .description(task.getDescription())
                            .number(task.getNumber())
                            .patternId(task.getPattern().getId())
                            .patternName(task.getPattern().getName())
                            .isSolved(isSolved)
                            .build();
                })
                .collect(Collectors.toList());
    }
}