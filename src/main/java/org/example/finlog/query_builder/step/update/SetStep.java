package org.example.finlog.query_builder.step.update;

import org.example.finlog.query_builder.step.general.ValueStep;
import org.example.finlog.query_builder.step.where.condition.ConditionStepForUpdate;

public interface SetStep {
    ValueStep<ConditionStepForUpdate> set(String... fields);
}
