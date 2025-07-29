package org.example.finlog.query_builder.builder;

import org.example.finlog.query_builder.statement.expression.BetweenExpression;
import org.example.finlog.query_builder.statement.expression.ComparisonExpression;
import org.example.finlog.query_builder.statement.expression.LogicalExpression;
import org.example.finlog.query_builder.statement.statement.AbstractStatementWhereClauseSupports;
import org.example.finlog.query_builder.util.Operation;
import org.example.finlog.query_builder.util.SqlKeyWord;

public abstract class AbstractBuilderHelperWhereClauseSupports {
    protected String currentField;
    protected String currentLogical;
    protected AbstractStatementWhereClauseSupports statement;

    public AbstractBuilderHelperWhereClauseSupports(AbstractStatementWhereClauseSupports statement) {
        this.statement = statement;
    }

    protected void appendComparison(Operation operation, Object value) {
        currentLogical = currentLogical == null ? SqlKeyWord.WHERE : currentLogical;

        statement.getWhere().add(new LogicalExpression(
                currentLogical,
                new ComparisonExpression(
                        currentField,
                        operation.getSign(),
                        value
                )));
    }

    protected void appendBetween(Object from, Object to) {
        statement.getWhere().add(new LogicalExpression(
                currentLogical,
                new BetweenExpression(
                        currentField,
                        from, to
                )));
    }
}
