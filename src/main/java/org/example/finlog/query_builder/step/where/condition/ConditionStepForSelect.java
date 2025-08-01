package org.example.finlog.query_builder.step.where.condition;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.LimitStep;
import org.example.finlog.query_builder.step.select.OrderStep;
import org.example.finlog.query_builder.step.where.logical.LogicalStepForSelect;

public interface ConditionStepForSelect extends
        ConditionStep<LogicalStepForSelect>,
        OrderStep,
        LimitStep,
        BuildStep {
}
