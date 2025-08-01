package org.example.finlog.query_builder.step.general;

public interface ValueStep<NEXT> {
    NEXT values(Object... values);
}
