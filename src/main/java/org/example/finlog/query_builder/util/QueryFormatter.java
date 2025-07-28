package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.statement.expression.BetweenExpression;
import org.example.finlog.query_builder.statement.expression.ComparisonExpression;
import org.example.finlog.query_builder.statement.expression.Expression;

public class QueryFormatter {
    public static String escapeIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    public static String escapeParameter(Object param) {
        if (param instanceof Number || param instanceof Boolean) {
            return param.toString();
        } else if (param == null) {
            return "NULL";
        }
        return "'" + param.toString().replace("'", "''") + "'";
    }

    public static String formatExpression(Expression expression) {
        if (expression instanceof ComparisonExpression) {
            return formatComparisonExpression(
                    (ComparisonExpression) expression
            );
        } else if (expression instanceof BetweenExpression) {
            return formatBetweenExpression(
                    (BetweenExpression) expression
            );
        }

        throw new RuntimeException(
                "Unexpected expression " +
                expression.getClass().getSimpleName()
        );
    }

    private static String formatComparisonExpression(ComparisonExpression e) {
        return escapeIdentifier(e.field()) + " " +
               e.operator() + " " +
               escapeParameter(e.value());
    }

    private static String formatBetweenExpression(BetweenExpression e) {
        return escapeIdentifier(e.field()) + " BETWEEN " +
               escapeParameter(e.from()) + " AND " +
               escapeParameter(e.to());
    }
}
