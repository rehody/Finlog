package org.example.finlog.query_builder.step.where.condition;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.where.logical.LogicalStepForUpdate;

public interface ConditionStepForUpdate extends
        ConditionStep<LogicalStepForUpdate>,
        BuildStep {
}
