package org.example.api.server.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.requests.SubmitRequestDto;
import org.example.api.dto.responses.SolvedTasksResponseDto;
import org.example.api.dto.responses.SubmitResponseDto;
import org.example.api.dto.responses.TaskResponseDto;
import org.example.services.SolutionService;
import org.example.services.TaskService;
import org.example.store.entities.SolutionEntity;
import org.example.util.JwtUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(name = "Solver Service", description = "API для решения задач и проверки кода")
@RequestMapping("/solver/v1")
public class SolverController {

    private final TaskService taskService;
    private final SolutionService solutionService;
    private final JwtUtil jwtUtil;

    @Operation(description = "Получить задачу по ID (userId из токена)")
    @GetMapping("/tasks/{taskId}")
    public TaskResponseDto getTask(@PathVariable Long taskId) {
        UUID userId = jwtUtil.getCurrentUserId();
        return taskService.getTaskById(taskId, userId);
    }

    @Operation(description = "Получить все задачи паттерна (userId из токена)")
    @GetMapping("/patterns/{patternId}/tasks")
    public List<TaskResponseDto> getTasksByPattern(@PathVariable Long patternId) {
        UUID userId = jwtUtil.getCurrentUserId();
        return taskService.getTasksByPatternId(patternId, userId);
    }

    @Operation(description = "Отправить решение задачи (userId из токена)")
    @PostMapping("/submit")
    public SubmitResponseDto submitSolution(@RequestBody SubmitRequestDto request) {
        // userId из токена имеет приоритет
        UUID userId = jwtUtil.getCurrentUserId();
        request.setUserId(userId);  // нужно добавить setter в DTO
        return solutionService.submitSolution(request);
    }

    @Operation(description = "Получить список решённых задач текущего пользователя")
    @GetMapping("/users/me/solved-tasks")
    public SolvedTasksResponseDto getMySolvedTasks() {
        UUID userId = jwtUtil.getCurrentUserId();
        List<Long> solvedTaskIds = solutionService.getSolvedTaskIdsByUser(userId);
        return SolvedTasksResponseDto.builder()
                .userId(userId)
                .solvedTaskIds(solvedTaskIds)
                .solvedCount(solvedTaskIds.size())
                .build();
    }

    @Operation(description = "Проверить, решена ли задача текущим пользователем")
    @GetMapping("/tasks/{taskId}/solved")
    public Boolean isTaskSolved(@PathVariable Long taskId) {
        UUID userId = jwtUtil.getCurrentUserId();
        return solutionService.isTaskSolved(taskId, userId);
    }
}