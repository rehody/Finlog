package org.example.finlog.query_builder.statement.statement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertStatement implements Statement {
    private String[] fields;
    private Object[] values;
    private String table;
}
