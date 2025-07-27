package org.example.finlog.query_builder.dsl;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.FromStep;
import org.example.finlog.query_builder.step.select.LimitStep;
import org.example.finlog.query_builder.step.select.OrderStep;
import org.example.finlog.query_builder.step.select.SelectStep;
import org.example.finlog.query_builder.step.where.ComparisonStep;
import org.example.finlog.query_builder.step.where.ConditionStep;
import org.example.finlog.query_builder.step.where.LogicalStep;
import org.example.finlog.query_builder.util.Operation;
import org.example.finlog.query_builder.util.QueryParser;
import org.example.finlog.query_builder.ast.expression.BetweenExpression;
import org.example.finlog.query_builder.ast.expression.ComparisonExpression;
import org.example.finlog.query_builder.ast.node.SelectStatement;

public class SelectBuilder implements
        SelectStep, FromStep, ConditionStep,
        ComparisonStep, LogicalStep, OrderStep, LimitStep, BuildStep {
    private final SelectStatement ast = new SelectStatement();
    private String currentField;

    private SelectBuilder() {
    }

    public static SelectStep builder() {
        return new SelectBuilder();
    }

    @Override
    public FromStep select(String... fields) {
        if (fields.length == 0) {
            ast.setFields(new String[]{"*"});
        } else {
            ast.setFields(fields);
        }

        return this;
    }

    @Override
    public ConditionStep from(String table) {
        ast.setTable(table);
        return this;
    }

    @Override
    public ComparisonStep where(String field) {
        currentField = field;
        return this;
    }

    @Override
    public LogicalStep eq(Object value) {
        appendComparison(Operation.EQUALS, value);
        return this;
    }

    @Override
    public LogicalStep lessThan(Object value) {
        appendComparison(Operation.LESS_THAN, value);
        return this;
    }

    @Override
    public LogicalStep greaterThan(Object value) {
        appendComparison(Operation.GREATER_THAN, value);
        return this;
    }

    @Override
    public LogicalStep notEq(Object value) {
        appendComparison(Operation.NOT_EQUALS, value);
        return this;
    }

    private void appendComparison(Operation operation, Object value) {
        ast.getWhere().add(new ComparisonExpression(
                currentField,
                operation.getSign(),
                value
        ));
    }

    @Override
    public LogicalStep between(Object from, Object to) {
        ast.getWhere().add(new BetweenExpression(
                currentField,
                from, to
        ));
        return this;
    }

    @Override
    public ComparisonStep and(String field) {
        currentField = field;
        return this;
    }

    @Override
    public ComparisonStep or(String field) {
        currentField = field;
        return this;
    }

    @Override
    public LimitStep orderBy(String field) {
        ast.setOrderBy(field);
        return this;
    }

    @Override
    public BuildStep limit(int limit) {
        ast.setLimit(limit);
        return this;
    }

    @Override
    public String build() {
        return QueryParser.parseSelect(ast);
    }
}
