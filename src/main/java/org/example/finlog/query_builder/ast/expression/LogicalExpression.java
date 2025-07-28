package org.example.finlog.query_builder.ast.expression;

public record LogicalExpression(
        String logicalOperator,
        Expression comparison
) implements Expression {
}
