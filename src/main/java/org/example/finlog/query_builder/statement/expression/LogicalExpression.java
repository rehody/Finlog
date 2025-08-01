package org.example.finlog.query_builder.statement.expression;

public record LogicalExpression(
        String logicalOperator,
        Expression comparison
) implements Expression {
}
