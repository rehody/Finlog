package org.example.finlog.query_builder.step.select;

import org.example.finlog.query_builder.step.general.BuildStep;

public interface LimitStep extends BuildStep {
    BuildStep limit(int limit);
}
