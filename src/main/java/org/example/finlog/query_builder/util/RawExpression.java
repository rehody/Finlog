package org.example.finlog.query_builder.util;

public record RawExpression(String expression) {
    public static RawExpression raw(String expression) {
        return new RawExpression(expression);
    }
}
