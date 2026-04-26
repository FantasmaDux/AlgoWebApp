package org.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на запрос входа пользователя")
public class TestResultDto {
    private Integer testNumber;
    private boolean passed;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private String errorMessage;
}
