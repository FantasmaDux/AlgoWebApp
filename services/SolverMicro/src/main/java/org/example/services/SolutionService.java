package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.TestResultDto;
import org.example.api.dto.requests.SubmitRequestDto;
import org.example.api.dto.responses.SubmitResponseDto;
import org.example.store.entities.SolutionEntity;
import org.example.store.entities.TestEntity;
import org.example.store.repositories.SolutionRepository;
import org.example.store.repositories.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final TestRepository testRepository;
    private final CompilerService compilerService;

    @Transactional
    public SubmitResponseDto submitSolution(SubmitRequestDto request) {
        // Получаем тесты для задачи
        List<TestEntity> tests = testRepository.findByTaskId(request.getTaskId());

        if (tests.isEmpty()) {
            return SubmitResponseDto.builder()
                    .success(false)
                    .status("ERROR")
                    .message("No tests found for this task")
                    .testResults(List.of())
                    .passedTests(0)
                    .totalTests(0)
                    .executionTimeMs(0L)
                    .build();
        }

        // Подготавливаем тест-кейсы
        List<CompilerService.TestCase> testCases = tests.stream()
                .map(t -> new CompilerService.TestCase(t.getInput(), t.getExpected()))
                .collect(Collectors.toList());

        // Компилируем и запускаем код
        CompilerService.ExecutionResult result = compilerService.compileAndRun(
                request.getCode(),
                request.getLanguage(),
                testCases
        );

        // Определяем статус
        String status;
        if (result.success()) {
            status = "SOLVED";
        } else if (result.error() != null && result.error().contains("Compilation error")) {
            status = "COMPILATION_ERROR";
        } else {
            status = "WRONG";
        }

        // Сохраняем решение в БД
        SolutionEntity solution = SolutionEntity.builder()
                .taskId(request.getTaskId())
                .userId(request.getUserId())
                .solution(request.getCode())
                .status(status)
                .submittedAt(Timestamp.from(Instant.now()))
                .build();
        solutionRepository.save(solution);

        // Формируем ответ
        return SubmitResponseDto.builder()
                .success(result.success())
                .status(status)
                .message(result.output() != null ? result.output() : result.error())
                .testResults(result.testResults())
                .passedTests((int) result.testResults().stream().filter(TestResultDto::isPassed).count())
                .totalTests(result.testResults().size())
                .executionTimeMs(result.executionTimeMs())
                .build();
    }

    public List<Long> getSolvedTaskIdsByUser(UUID userId) {
        return solutionRepository.findByUserId(userId).stream()
                .filter(s -> "SOLVED".equals(s.getStatus()))
                .map(SolutionEntity::getTaskId)
                .collect(Collectors.toList());
    }

    public boolean isTaskSolved(Long taskId, UUID userId) {
        return solutionRepository.existsByTaskIdAndUserIdAndStatus(taskId, userId, "SOLVED");
    }
}