package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.statement.expression.BetweenExpression;
import org.example.finlog.query_builder.statement.expression.ComparisonExpression;
import org.example.finlog.query_builder.statement.expression.Expression;

import java.util.Arrays;
import java.util.function.Function;

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

    public static String formatToSequence(Object[] values) {
        Function<Object, String> func;
        if (values instanceof String[]) {
            func = obj -> QueryFormatter.escapeIdentifier((String) obj);
        } else {
            func = QueryFormatter::escapeParameter;
        }

        return String.join(", ",
                Arrays.stream(values)
                        .map(func)
                        .toList()
        );
    }

    private static String formatComparisonExpression(ComparisonExpression e) {
        return escapeIdentifier(e.field()) + " " +
               e.operator() + " " +
               escapeParameter(e.value());
    }

    private static String formatBetweenExpression(BetweenExpression e) {
        return escapeIdentifier(e.field()) + " " +
               SqlKeyWord.BETWEEN + " " +
               escapeParameter(e.from()) + " " +
               SqlKeyWord.AND + " " +
               escapeParameter(e.to());
    }
}
