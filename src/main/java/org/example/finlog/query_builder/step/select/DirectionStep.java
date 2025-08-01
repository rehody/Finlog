package org.example.finlog.query_builder.step.select;

import org.example.finlog.query_builder.step.general.BuildStep;
import org.example.finlog.query_builder.util.OrderDirection;

public interface DirectionStep extends LimitStep, BuildStep {
    LimitStep direction(OrderDirection direction);
}
