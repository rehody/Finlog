package org.example.finlog.query_builder.dsl;

import org.example.finlog.query_builder.statement.node.InsertStatement;
import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.insert.FieldStep;
import org.example.finlog.query_builder.step.insert.InsertStep;
import org.example.finlog.query_builder.step.insert.ValueStep;
import org.example.finlog.query_builder.util.QueryParser;

public class InsertBuilder implements InsertStep, FieldStep, ValueStep, BuildStep {
    private final InsertStatement statement = new InsertStatement();
    private int fieldsCount;

    public static InsertStep builder() {
        return new InsertBuilder();
    }

    @Override
    public FieldStep insertInto(String table) {
        statement.setTable(table);
        return this;
    }

    @Override
    public ValueStep fields(String... fields) {
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
