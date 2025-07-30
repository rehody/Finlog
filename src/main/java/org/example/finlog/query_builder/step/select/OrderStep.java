package org.example.finlog.query_builder.step.select;

import org.example.finlog.query_builder.step.general.BuildStep;

public interface OrderStep extends LimitStep, BuildStep {
    DirectionStep orderBy(String... field);
}
