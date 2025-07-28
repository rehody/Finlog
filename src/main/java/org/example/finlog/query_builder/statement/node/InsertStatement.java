package org.example.finlog.query_builder.statement.node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertStatement implements QueryNode {
    private String[] fields;
    private Object[] values;
    private String table;
}
