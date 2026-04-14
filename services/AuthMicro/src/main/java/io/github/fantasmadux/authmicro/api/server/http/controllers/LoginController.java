package io.github.fantasmadux.authmicro.api.server.http.controllers;

import io.github.fantasmadux.authmicro.annotations.CommonApiResponses;
import io.github.fantasmadux.authmicro.api.dto.ErrorDto;
import io.github.fantasmadux.authmicro.api.dto.requests.LoginConfirmRequestDto;
import io.github.fantasmadux.authmicro.api.dto.requests.LoginRequestDto;
import io.github.fantasmadux.authmicro.api.dto.responses.LoginConfirmResponseDto;
import io.github.fantasmadux.authmicro.api.dto.responses.LoginResponseDto;
import io.github.fantasmadux.authmicro.services.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Логин пользователя", description = "API для работы с входом пользователя")
@RequestMapping("/auth/v1/login")
public class LoginController {
    private final LoginService loginService;

    @Operation(description = "Метод отправки кода для подтверждения входа на почту пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Код подтверждения отправлен на почту пользователя"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверно указаны данные",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    name = "ValidationError",
                                    summary = "Ошибка логина.",
                                    value = """
                                            {
                                              "error": "Ошибка логина.",
                                              "detailedErrors": [
                                                {
                                                  "field": "email",
                                                  "message": "Некорректный формат Email."
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @CommonApiResponses
    @PostMapping(value = "/sendCodeEmail", produces = "application/json")
    public LoginResponseDto sendLoginCode(
            @RequestBody LoginRequestDto loginRequest
    ) {
        // TODO: send email
        return loginService.login(loginRequest.getEmail());
    }

    @Operation(description = "Метод подтверждения входа пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный вход. Токены выданы."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверно указаны данные",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "ValidationError",
                                            summary = "Ошибка валидации",
                                            value = """
                                                    {
                                                      "error": "Ошибка валидации.",
                                                      "detailedErrors": [
                                                        {
                                                          "field": "email",
                                                          "message": "Некорректный формат Email."
                                                        }
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "InvalidCodeException",
                                            summary = "Неверный код подтверждения",
                                            value = """
                                                    {
                                                      "error": "Ошибка подтверждения кода.",
                                                      "detailedErrors": [
                                                        {
                                                          "field": "code",
                                                          "message": "Неверный код подтверждения."
                                                        }
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "CodeExpiredException",
                                            summary = "Код подтверждения истёк",
                                            value =
                                                    """
                                                    {
                                                      "error": "Ошибка подтверждения кода.",
                                                      "detailedErrors": [
                                                        {
                                                          "field": "code",
                                                          "message": "Код подтверждения истёк. Пожалуйста, запросите новый код."
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @CommonApiResponses
    @PostMapping(value = "/confirmEmail", produces = "application/json")
    public LoginConfirmResponseDto confirmLoginEmail(
            @RequestBody LoginConfirmRequestDto loginConfirmRequest,
            HttpServletRequest httpRequest
    ) {
        String ip;
        String header = httpRequest.getHeader("X-Forwarded-For");
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            ip = header.split(",")[0].trim();
        } else {
            ip = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return loginService.confirmLoginEmail(
                loginConfirmRequest.getEmail(),
                loginConfirmRequest.getCode(),
                ip, userAgent
        );
    }
}
