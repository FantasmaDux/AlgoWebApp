package org.example.api.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SolvedTasksResponseDto {
    private UUID userId;
    private List<Long> solvedTaskIds;
    private Integer solvedCount;
}
