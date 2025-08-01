package org.example.finlog.query_builder.step.where.logical;

import org.example.finlog.query_builder.step.where.ComparisonStep;

public interface LogicalStep<NEXT extends LogicalStep<NEXT>> {
    ComparisonStep<NEXT> and(String field);
    ComparisonStep<NEXT> or(String field);
}
