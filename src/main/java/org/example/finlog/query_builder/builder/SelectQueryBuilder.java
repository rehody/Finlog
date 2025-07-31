package org.example.finlog.query_builder.builder;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.*;
import org.example.finlog.query_builder.step.where.ComparisonStep;
import org.example.finlog.query_builder.step.where.ConditionStepForSelect;
import org.example.finlog.query_builder.step.where.LogicalStepForSelect;
import org.example.finlog.query_builder.util.Operation;
import org.example.finlog.query_builder.util.OrderDirection;
import org.example.finlog.query_builder.util.QuerySerializer;
import org.example.finlog.query_builder.statement.statement.SelectStatement;
import org.example.finlog.query_builder.util.SqlKeyWord;

public class SelectQueryBuilder extends AbstractBuilderHelperWhereClauseSupports
        implements
        SelectStep,
        FromStep,
        ConditionStepForSelect,
        ComparisonStep<LogicalStepForSelect>,
        LogicalStepForSelect,
        OrderStep,
        DirectionStep,
        LimitStep,
        BuildStep {
    private final SelectStatement statement;

    private SelectQueryBuilder(SelectStatement statement) {
        super(statement);
        this.statement = statement;
    }

    public static SelectStep builder() {
        return new SelectQueryBuilder(new SelectStatement());
    }

    @Override
    public FromStep select(String... fields) {
        if (fields.length != 0) {
            statement.setFields(fields);
        }

        return this;
    }

    @Override
    public ConditionStepForSelect from(String table) {
        statement.setTable(table);
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForSelect> where(String field) {
        currentField = field;
        return this;
    }

    @Override
    public LogicalStepForSelect eq(Object value) {
        appendComparison(Operation.EQUALS, value);
        return this;
    }

    @Override
    public LogicalStepForSelect lessThan(Object value) {
        appendComparison(Operation.LESS_THAN, value);
        return this;
    }

    @Override
    public LogicalStepForSelect greaterThan(Object value) {
        appendComparison(Operation.GREATER_THAN, value);
        return this;
    }

    @Override
    public LogicalStepForSelect notEq(Object value) {
        appendComparison(Operation.NOT_EQUALS, value);
        return this;
    }

    @Override
    public LogicalStepForSelect between(Object from, Object to) {
        appendBetween(from, to);
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForSelect> and(String field) {
        currentLogical = SqlKeyWord.AND;
        currentField = field;
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForSelect> or(String field) {
        currentLogical = SqlKeyWord.OR;
        currentField = field;
        return this;
    }

    @Override
    public DirectionStep orderBy(String... field) {
        statement.setOrderBy(field);
        return this;
    }

    @Override
    public LimitStep direction(OrderDirection direction) {
        statement.setOrderDirection(direction);
        return this;
    }

    @Override
    public BuildStep limit(int limit) {
        statement.setLimit(limit);
        return this;
    }

    @Override
    public String build() {
        return QuerySerializer.Parser.parseSelect(statement);
    }
}
