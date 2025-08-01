package org.example.finlog.query_builder.step.insert;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.general.ValueStep;

public interface FieldStep {
    ValueStep<BuildStep> fields(String... fields);
}
