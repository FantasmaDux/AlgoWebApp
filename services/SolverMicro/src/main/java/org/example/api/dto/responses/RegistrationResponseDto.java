package org.example.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на запрос регистрации пользователя")
public class RegistrationResponseDto {
    @Schema(description = "Время истечения кода подтверждения для входа")
    private long codeExpires;
    @Schema(description = "Паттерн генерации кода")
    private String codePattern;
}
