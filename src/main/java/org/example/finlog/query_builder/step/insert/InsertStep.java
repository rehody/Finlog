package org.example.finlog.query_builder.step.insert;

public interface InsertStep {
    FieldStep insertInto(String table);
}
