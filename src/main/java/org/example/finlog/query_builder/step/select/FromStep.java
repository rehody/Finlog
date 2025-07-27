package org.example.finlog.query_builder.step.select;

import org.example.finlog.query_builder.step.where.ConditionStep;

public interface FromStep {
    ConditionStep from(String table);
}
