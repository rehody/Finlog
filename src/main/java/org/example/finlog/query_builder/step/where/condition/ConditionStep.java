package org.example.finlog.query_builder.step.where.condition;

import org.example.finlog.query_builder.step.where.ComparisonStep;
import org.example.finlog.query_builder.step.where.logical.LogicalStep;

public interface ConditionStep<NEXT extends LogicalStep<NEXT>> {
    ComparisonStep<NEXT> where(String field);
}
