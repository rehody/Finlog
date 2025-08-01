package org.example.finlog.query_builder.statement.statement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatement extends AbstractStatementWhereClauseSupports implements Statement {
    private String table;
    private String[] fields;
    private Object[] values;
}
