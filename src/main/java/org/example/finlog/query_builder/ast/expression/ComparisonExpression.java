package org.example.finlog.query_builder.ast.expression;

public record ComparisonExpression(
        String field,
        String operator,
        Object value
) implements Expression {
}
