package org.example.services;

import lombok.Data;
import org.example.api.dto.TestResultDto;

import java.util.List;

public interface CompilerService {
    ExecutionResult compileAndRun(String code, String language, List<TestCase> testCases);

    record TestCase(String input, String expected) {}

    record ExecutionResult(
            boolean success,
            String output,
            String error,
            Long executionTimeMs,
            List<TestResultDto> testResults
    ) {}

}