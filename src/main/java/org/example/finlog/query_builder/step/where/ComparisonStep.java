package org.example.finlog.query_builder.step.where;

public interface ComparisonStep {
    LogicalStep eq(Object value);
    LogicalStep lessThan(Object value);
    LogicalStep greaterThan(Object value);
    LogicalStep between(Object from, Object to);
    LogicalStep notEq(Object value);
}
