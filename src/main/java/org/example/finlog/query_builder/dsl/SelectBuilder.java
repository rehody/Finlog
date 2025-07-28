package org.example.finlog.query_builder.dsl;

import org.example.finlog.query_builder.statement.expression.LogicalExpression;
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
import org.example.finlog.query_builder.statement.expression.BetweenExpression;
import org.example.finlog.query_builder.statement.expression.ComparisonExpression;
import org.example.finlog.query_builder.statement.node.SelectStatement;
import org.example.finlog.query_builder.util.SqlKeyWord;

public class SelectBuilder implements
        SelectStep, FromStep, ConditionStep,
        ComparisonStep, LogicalStep, OrderStep, LimitStep, BuildStep {
    private final SelectStatement statement = new SelectStatement();
    private String currentField;
    private String currentLogical;

    private SelectBuilder() {
    }

    public static SelectStep builder() {
        return new SelectBuilder();
    }

    @Override
    public FromStep select(String... fields) {
        if (fields.length != 0) {
            statement.setFields(fields);
        }

        return this;
    }

    @Override
    public ConditionStep from(String table) {
        statement.setTable(table);
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
        currentLogical = currentLogical == null ? SqlKeyWord.WHERE : currentLogical;

        statement.getWhere().add(new LogicalExpression(
                currentLogical,
                new ComparisonExpression(
                        currentField,
                        operation.getSign(),
                        value
                )));
    }

    @Override
    public LogicalStep between(Object from, Object to) {
        statement.getWhere().add(new LogicalExpression(
                currentLogical,
                new BetweenExpression(
                        currentField,
                        from, to
                )));
        return this;
    }

    @Override
    public ComparisonStep and(String field) {
        currentLogical = SqlKeyWord.AND;
        currentField = field;
        return this;
    }

    @Override
    public ComparisonStep or(String field) {
        currentLogical = SqlKeyWord.OR;
        currentField = field;
        return this;
    }

    @Override
    public LimitStep orderBy(String field) {
        statement.setOrderBy(field);
        return this;
    }

    @Override
    public BuildStep limit(int limit) {
        statement.setLimit(limit);
        return this;
    }

    @Override
    public String build() {
        return QueryParser.parseSelect(statement);
    }
}
