package io.github.fantasmadux.authmicro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ ошибки валидации полей")
public class ErrorDto {
    @Schema(description = "Общее название ошибки для отображения пользователю")
    private String error;
    @Schema(description = "Список ошибок")
    private List<FieldErrorDto> detailedErrors;
}
