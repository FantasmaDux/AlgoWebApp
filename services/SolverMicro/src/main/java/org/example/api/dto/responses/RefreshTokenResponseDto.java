package org.example.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на запрос обновления токенов пользователя")
public class RefreshTokenResponseDto {
    @Schema(description = "Обновлённый refresh токен пользователя")
    private String refreshToken;
    @Schema(description = "Время истечения refresh токена пользователя")
    private long refreshTokenExpires;
    @Schema(description = "Обновлённый access токен пользователя")
    private String accessToken;
    @Schema(description = "Время истечения access токена пользователя")
    private long accessTokenExpires;

}
