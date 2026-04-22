package org.example.api.exceptions;

import org.springframework.http.HttpStatus;

public class ServerAnswerException extends HttpStatusException {
    public ServerAnswerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
