package org.example.finlog.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_ERROR("INTERNAL_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    NOT_FOUND("NOT_FOUND"),
    FORBIDDEN("FORBIDDEN"),
    BAD_REQUEST("BAD_REQUEST"),
    CONFLICT("CONFLICT");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }
}