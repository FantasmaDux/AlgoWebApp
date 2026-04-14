package io.github.fantasmadux.authmicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос подтверждения логина")
public class LoginConfirmRequestDto {

    @Schema(description = "Почта пользователя")
    private String email;

    @Schema(description = "Код подтвеерждения, полученный пользователем по почте")
    private String code;
}
