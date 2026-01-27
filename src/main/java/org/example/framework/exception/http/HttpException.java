package org.example.framework.exception.http;

import org.example.framework.was.protocol.model.HttpStatus;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
