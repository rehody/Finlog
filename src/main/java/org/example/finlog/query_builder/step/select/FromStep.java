package org.example.finlog.query_builder.step.select;

import org.example.finlog.query_builder.step.where.condition.ConditionStepForSelect;

public interface FromStep {
    ConditionStepForSelect from(String table);
}
