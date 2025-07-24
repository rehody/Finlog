package org.example.finlog.DTO;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path
) {
    public ErrorResponse(String code, String message, String path) {
        this(code, message, Instant.now(), path);
    }
}
