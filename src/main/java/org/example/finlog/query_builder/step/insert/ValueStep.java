package org.example.finlog.query_builder.step.insert;

import org.example.finlog.query_builder.step.general.BuildStep;

public interface ValueStep extends BuildStep {
    BuildStep values(Object... values);
}
