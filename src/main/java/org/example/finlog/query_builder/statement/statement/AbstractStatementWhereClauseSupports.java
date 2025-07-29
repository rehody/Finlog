package org.example.finlog.query_builder.statement.statement;

import lombok.Getter;
import lombok.Setter;
import org.example.finlog.query_builder.statement.expression.LogicalExpression;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractStatementWhereClauseSupports implements Statement {
    protected List<LogicalExpression> where = new ArrayList<>();
}
