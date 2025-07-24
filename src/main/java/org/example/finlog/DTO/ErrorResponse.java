package org.example.finlog.DTO;

import org.example.finlog.util.ErrorCode;

import java.time.Instant;

public record ErrorResponse(
        ErrorCode code,
        String message,
        Instant timestamp,
        String path
) {
    public ErrorResponse(ErrorCode code, String message, String path) {
        this(code, message, Instant.now(), path);
    }
}
