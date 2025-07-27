package org.example.finlog.query_builder.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {
    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    NOT_EQUALS("!=");

    private final String sign;
}
