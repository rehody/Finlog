package org.example.finlog.query_builder.builder;

import org.example.finlog.query_builder.statement.statement.UpdateStatement;
import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.general.ValueStep;
import org.example.finlog.query_builder.step.update.SetStep;
import org.example.finlog.query_builder.step.update.UpdateStep;
import org.example.finlog.query_builder.step.where.ComparisonStep;
import org.example.finlog.query_builder.step.where.ConditionStepForUpdate;
import org.example.finlog.query_builder.step.where.LogicalStepForUpdate;
import org.example.finlog.query_builder.util.Operation;
import org.example.finlog.query_builder.util.QuerySerializer;
import org.example.finlog.query_builder.util.SqlKeyWord;

public class UpdateQueryBuilder extends AbstractBuilderHelperWhereClauseSupports
        implements
        UpdateStep,
        SetStep,
        ValueStep<ConditionStepForUpdate>,
        ConditionStepForUpdate,
        ComparisonStep<LogicalStepForUpdate>,
        LogicalStepForUpdate,
        BuildStep {
    UpdateStatement statement;
    private int fieldsCount;

    private UpdateQueryBuilder(UpdateStatement statement) {
        super(statement);
        this.statement = statement;
    }

    public static UpdateStep builder() {
        return new UpdateQueryBuilder(new UpdateStatement());
    }

    @Override
    public SetStep update(String table) {
        statement.setTable(table);
        return this;
    }

    @Override
    public ValueStep<ConditionStepForUpdate> set(String... fields) {
        if (fields.length == 0) {
            throw new IllegalStateException(
                    "Zero fields are not allowed"
            );
        }

        fieldsCount = fields.length;
        statement.setFields(fields);
        return this;
    }

    @Override
    public ConditionStepForUpdate values(Object... values) {
        if (values.length != fieldsCount) {
            throw new IllegalStateException(
                    "The number of VALUES parameters must match with the number of fields"
            );
        }

        statement.setValues(values);
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForUpdate> where(String field) {
        currentField = field;
        return this;
    }

    @Override
    public LogicalStepForUpdate eq(Object value) {
        appendComparison(Operation.EQUALS, value);
        return this;
    }

    @Override
    public LogicalStepForUpdate lessThan(Object value) {
        appendComparison(Operation.LESS_THAN, value);
        return this;
    }

    @Override
    public LogicalStepForUpdate greaterThan(Object value) {
        appendComparison(Operation.GREATER_THAN, value);
        return this;
    }

    @Override
    public LogicalStepForUpdate notEq(Object value) {
        appendComparison(Operation.NOT_EQUALS, value);
        return this;
    }

    @Override
    public LogicalStepForUpdate between(Object from, Object to) {
        appendBetween(from, to);
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForUpdate> and(String field) {
        currentLogical = SqlKeyWord.AND;
        currentField = field;
        return this;
    }

    @Override
    public ComparisonStep<LogicalStepForUpdate> or(String field) {
        currentLogical = SqlKeyWord.OR;
        currentField = field;
        return this;
    }

    @Override
    public String build() {
        return QuerySerializer.Parser.parseUpdate(statement);
    }
}
