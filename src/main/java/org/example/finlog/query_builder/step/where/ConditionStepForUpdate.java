package org.example.finlog.query_builder.step.where;

import org.example.finlog.query_builder.step.general.BuildStep;

public interface ConditionStepForUpdate extends BuildStep {
    ComparisonStep<LogicalStepForUpdate> where(String field);
}
