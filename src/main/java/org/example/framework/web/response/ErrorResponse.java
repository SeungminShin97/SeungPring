package org.example.framework.web.response;

import org.example.framework.exception.http.HttpException;
import org.example.framework.was.protocol.model.HttpStatus;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        long timestamp
) {
    public static ErrorResponse from(HttpException e, String path) {
        HttpStatus status = e.getStatus();
        return new ErrorResponse(status.code(), status.reason(), e.getMessage(), path, System.currentTimeMillis());
    }
}
