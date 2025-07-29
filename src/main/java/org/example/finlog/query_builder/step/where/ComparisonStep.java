package org.example.finlog.query_builder.step.where;

public interface ComparisonStep<NEXT> {
    NEXT eq(Object value);
    NEXT lessThan(Object value);
    NEXT greaterThan(Object value);
    NEXT between(Object from, Object to);
    NEXT notEq(Object value);
}
