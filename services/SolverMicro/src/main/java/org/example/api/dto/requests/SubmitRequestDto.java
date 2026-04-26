package org.example.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "")
public class SubmitRequestDto {
    private Long taskId;
    private UUID userId;
    private String code;
    private String language;
}
