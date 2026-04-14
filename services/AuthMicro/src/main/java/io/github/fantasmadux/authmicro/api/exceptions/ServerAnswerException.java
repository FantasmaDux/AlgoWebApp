package io.github.fantasmadux.authmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ServerAnswerException extends HttpStatusException {
    public ServerAnswerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
