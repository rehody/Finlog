package org.example.finlog.query_builder.builder;

import org.example.finlog.query_builder.statement.statement.InsertStatement;
import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.insert.FieldStep;
import org.example.finlog.query_builder.step.insert.InsertStep;
import org.example.finlog.query_builder.step.general.ValueStep;
import org.example.finlog.query_builder.util.QueryParser;

public class InsertQueryBuilder implements
        InsertStep,
        FieldStep,
        ValueStep<BuildStep>,
        BuildStep
{
    private final InsertStatement statement = new InsertStatement();
    private int fieldsCount;

    private InsertQueryBuilder() {
    }

    public static InsertStep builder() {
        return new InsertQueryBuilder();
    }

    @Override
    public FieldStep insertInto(String table) {
        statement.setTable(table);
        return this;
    }

    @Override
    public ValueStep<BuildStep> fields(String... fields) {
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
    public BuildStep values(Object... values) {
        if (values.length != fieldsCount) {
            throw new IllegalStateException(
                    "The number of VALUES parameters must match with the number of fields"
            );
        }

        statement.setValues(values);
        return this;
    }

    @Override
    public String build() {
        return QueryParser.parseInsert(statement);
    }
}
