package io.github.fantasmadux.authmicro.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpStatusException extends RuntimeException{
    private final HttpStatus status;

    public HttpStatusException(HttpStatus status) {
        this.status = status;
    }
}
