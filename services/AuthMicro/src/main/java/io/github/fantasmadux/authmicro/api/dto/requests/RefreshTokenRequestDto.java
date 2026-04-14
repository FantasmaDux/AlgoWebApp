package io.github.fantasmadux.authmicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на обновление токенов refresh и access")
public class RefreshTokenRequestDto {
    @Schema(description = "Refresh токен для обновления")
    private String refreshToken;
}
