package org.example.finlog.query_builder.step.where;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.LimitStep;
import org.example.finlog.query_builder.step.select.OrderStep;

public interface ConditionStepForSelect extends OrderStep, LimitStep, BuildStep {
    ComparisonStep<LogicalStepForSelect> where(String field);
}
