package io.github.fantasmadux.authmicro.api.server.http.controllers;

import io.github.fantasmadux.authmicro.annotations.CommonApiResponses;
import io.github.fantasmadux.authmicro.api.dto.requests.RefreshTokenRequestDto;
import io.github.fantasmadux.authmicro.api.dto.responses.RefreshTokenResponseDto;
import io.github.fantasmadux.authmicro.services.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Обновление токенов пользователя", description = "API для работы с токенами пользователя")
@RequestMapping("/auth/v1")
public class RefreshTokensController {
    private final RefreshTokenService refreshTokenService;

    @Operation(description = "Метод обновления access и refresh токенов пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Выданы обновлённые токены"
            )
    })
    @CommonApiResponses
    @PostMapping(value = "/refreshToken", produces = "application/json")
    public RefreshTokenResponseDto refreshToken(
            @RequestBody RefreshTokenRequestDto refreshTokenRequest
    ) {
        return refreshTokenService.refreshTokens(refreshTokenRequest.getRefreshToken());

    }
}
