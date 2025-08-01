package org.example.finlog.query_builder.step.where;

import org.example.finlog.query_builder.step.general.BuildStep;

public interface LogicalStepForUpdate extends BuildStep {
    ComparisonStep<LogicalStepForUpdate> and(String field);
    ComparisonStep<LogicalStepForUpdate> or(String field);
}