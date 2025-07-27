package org.example.finlog.query_builder.step.select;

public interface SelectStep {
    FromStep select(String... fields);
}
