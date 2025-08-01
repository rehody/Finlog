package org.example.finlog.query_builder.step.where.logical;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.LimitStep;
import org.example.finlog.query_builder.step.select.OrderStep;

public interface LogicalStepForSelect extends
        LogicalStep<LogicalStepForSelect>,
        OrderStep,
        LimitStep,
        BuildStep {
}
