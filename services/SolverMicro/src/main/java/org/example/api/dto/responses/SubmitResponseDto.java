package org.example.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.api.dto.TestResultDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "")
public class SubmitResponseDto {
    private boolean success;
    private String status;
    private String message;
    private List<TestResultDto> testResults;
    private Long executionTimeMs;
    private Integer passedTests;
    private Integer totalTests;
}
