package io.github.fantasmadux.authmicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на запрос входа пользователя")
public class LoginConfirmResponseDto {
    @Schema(description = "Refresh токен пользователя")
    private String refreshToken;
    @Schema(description = "Время истечения refresh токена пользователя")
    private long refreshTokenExpires;
    @Schema(description = "Access токен пользователя")
    private String accessToken;
    @Schema(description = "Время истечения access токена пользователя")
    private long accessTokenExpires;
}
