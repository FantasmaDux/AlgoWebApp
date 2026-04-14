package io.github.fantasmadux.authmicro.api.server.http.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.fantasmadux.authmicro.annotations.CommonApiResponses;
import io.github.fantasmadux.authmicro.api.dto.ErrorDto;
import io.github.fantasmadux.authmicro.api.dto.responses.RegistrationResponseDto;
import io.github.fantasmadux.authmicro.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Регистрация пользователя", description = "API для работы с регистрацией пользователя")
@RequestMapping("/auth/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Operation(description = "Метод отправки кода для подтверждения регистрации на почту пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Код подтверждения отправлен на почту пользователя"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверно указаны данные",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class),
                            examples = {
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
                                            value =  """
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
                                    ),
                                    @ExampleObject(
                                            name = "FieldValidationException",
                                            summary = "Ошибка регистрации",
                                            value = """
                                                    {
                                                      "error": "Ошибка регистрации",
                                                      "detailedErrors": [
                                                        {
                                                          "field": "email",
                                                          "message": "Некорректный формат Email."
                                                        },
                                                        {
                                                          "field": "firstName",
                                                          "message": "Поле пустое."
                                                        },
                                                        {
                                                          "field": "acceptedPrivacyPolicy",
                                                          "message": "Не принято пользовательское соглашение."
                                                        },
                                                        {
                                                          "field": "acceptedPersonalDataProcessing",
                                                          "message": "Не принято соглашение на обработку персональных данных."
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Список обновляемых полей аккаунта",
            content = @Content(
                    schema = @Schema(type = "object"),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                                    {
                                      "email": "user108@communicator.ru",
                                      "acceptedPrivacyPolicy": true,
                                      "acceptedPersonalDataProcessing": true,
                                      "firstName": "Jovany"
                                    }
                                    """
                    )
            )
    )
    @PostMapping(value = "", produces = "application/json")
    public RegistrationResponseDto sendRegistrationCode(@RequestBody JsonNode registrationRequest) {
        return registrationService.register(registrationRequest);
    }

    @Operation(description = "Метод подтверждения регистрации пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь зарегистрирован"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверно указаны данные",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class),
                            examples = {
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
                                            value =  """
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
                                    ),
                                    @ExampleObject(
                                            name = "FieldValidationException",
                                            summary = "Ошибка регистрации",
                                            value = """
                                                    {
                                                      "error": "Ошибка регистрации",
                                                      "detailedErrors": [
                                                        {
                                                          "field": "email",
                                                          "message": "Некорректный формат Email."
                                                        },
                                                        {
                                                          "field": "firstName",
                                                          "message": "Поле пустое."
                                                        },
                                                        {
                                                          "field": "acceptedPrivacyPolicy",
                                                          "message": "Не принято пользовательское соглашение."
                                                        },
                                                        {
                                                          "field": "acceptedPersonalDataProcessing",
                                                          "message": "Не принято соглашение на обработку персональных данных."
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Список обновляемых полей аккаунта",
            content = @Content(
                    schema = @Schema(type = "object"),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                                    {
                                      "email": "user108@communicator.ru",
                                      "acceptedPrivacyPolicy": true,
                                      "acceptedPersonalDataProcessing": true,
                                      "firstName": "Jovany",
                                      "code" : "856868"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/confirmEmail")
    public ResponseEntity<Void> registrationConfirmEmail(
            @RequestBody JsonNode registrationConfirmRequest,
            HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        registrationService.confirmEmail(
                registrationConfirmRequest, ip);
        return ResponseEntity.ok().build();
    }
}
