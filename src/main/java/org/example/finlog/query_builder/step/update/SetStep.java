package org.example.finlog.query_builder.step.update;

import org.example.finlog.query_builder.step.general.ValueStep;
import org.example.finlog.query_builder.step.where.ConditionStepForUpdate;

public interface SetStep {
    ValueStep<ConditionStepForUpdate> set(String... fields);
}
