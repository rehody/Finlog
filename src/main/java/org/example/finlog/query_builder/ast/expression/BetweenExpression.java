package org.example.finlog.query_builder.ast.expression;

public record BetweenExpression(
        String field,
        Object from,
        Object to
) implements Expression {
}
