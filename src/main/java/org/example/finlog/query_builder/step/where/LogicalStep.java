package org.example.finlog.query_builder.step.where;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.step.select.LimitStep;
import org.example.finlog.query_builder.step.select.OrderStep;

public interface LogicalStep extends OrderStep, LimitStep, BuildStep {
    ComparisonStep and(String field);
    ComparisonStep or(String field);
}
