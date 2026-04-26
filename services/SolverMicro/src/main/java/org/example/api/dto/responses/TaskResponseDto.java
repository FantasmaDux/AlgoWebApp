package org.example.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "")
public class TaskResponseDto {
    private Long id;
    private String name;
    private String description;
    private Integer number;
    private Long patternId;
    private String patternName;
    private Boolean isSolved;
}
