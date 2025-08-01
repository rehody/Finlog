package org.example.finlog.query_builder.statement.statement;

import lombok.Getter;
import lombok.Setter;
import org.example.finlog.query_builder.util.OrderDirection;

@Getter
@Setter
public class SelectStatement extends AbstractStatementWhereClauseSupports implements Statement {
    private String[] fields;
    private String table;
    private String[] orderBy;
    private OrderDirection orderDirection = OrderDirection.ASC;
    private Integer limit;
}
