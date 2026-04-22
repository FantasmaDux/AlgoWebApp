package org.example.api.server.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "Логин пользователя", description = "API для работы с входом пользователя")
@RequestMapping("/solver/v1")
public class SolverController {

    @Operation(description = "Метод отправки кода для подтверждения входа на почту пользователя")
    @org.example.annotations.CommonApiResponses
    @PostMapping(value = "/checkSolution", produces = "application/json")
    public LoginResponseDto sendLoginCode(
            @RequestBody LoginRequestDto loginRequest
    ) {
        // TODO: send email
        return loginService.login(loginRequest.getEmail());
    }

    @GetMapping(value = "/task", produces = "application/json")
    public LoginResponseDto sendLoginCode(
            @RequestBody LoginRequestDto loginRequest
    ) {
        // TODO: send email
        return loginService.login(loginRequest.getEmail());
    }
}
