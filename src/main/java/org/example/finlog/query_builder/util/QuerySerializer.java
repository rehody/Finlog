package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.statement.expression.BetweenExpression;
import org.example.finlog.query_builder.statement.expression.ComparisonExpression;
import org.example.finlog.query_builder.statement.expression.Expression;
import org.example.finlog.query_builder.statement.expression.LogicalExpression;
import org.example.finlog.query_builder.statement.statement.AbstractStatementWhereClauseSupports;
import org.example.finlog.query_builder.statement.statement.InsertStatement;
import org.example.finlog.query_builder.statement.statement.SelectStatement;
import org.example.finlog.query_builder.statement.statement.UpdateStatement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class QuerySerializer {

    private QuerySerializer() {
    }

    public static final class Formatter {

        public static String escapeIdentifier(String identifier) {
            return "\"" + identifier.replace("\"", "\"\"") + "\"";
        }

        public static String escapeParameter(Object param) {
            if (param instanceof Number || param instanceof Boolean) {
                return param.toString();
            } else if (param instanceof RawExpression) {
                return ((RawExpression) param).expression();
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
                func = obj -> escapeIdentifier((String) obj);
            } else {
                func = Formatter::escapeParameter;
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


    public static final class Parser {

        public static String parseSelect(SelectStatement statement) {
            StringBuilder query = new StringBuilder();

            String formattedFields = statement.getFields() == null ? "*" :
                    Formatter.formatToSequence(statement.getFields());

            String escapedTable = Formatter
                    .escapeIdentifier(statement.getTable());

            query.append(SqlKeyWord.SELECT).append(" ")
                    .append(formattedFields).append(" ")
                    .append(SqlKeyWord.FROM).append(" ")
                    .append(escapedTable).append(" ");

            appendWhere(statement, query);
            appendOrderBy(statement, query);
            appendLimit(statement, query);

            return query.toString().trim();
        }

        public static String parseInsert(InsertStatement statement) {
            StringBuilder query = new StringBuilder();

            String formattedFields = Formatter.formatToSequence(statement.getFields());
            String formattedValues = Formatter.formatToSequence(statement.getValues());
            String escapedTable = Formatter.escapeIdentifier(statement.getTable());

            query.append(SqlKeyWord.INSERT_INTO).append(" ")
                    .append(escapedTable).append(" (")
                    .append(formattedFields).append(") ")
                    .append(SqlKeyWord.VALUES).append(" (")
                    .append(formattedValues).append(") ");

            return query.toString().trim();
        }

        public static String parseUpdate(UpdateStatement statement) {
            StringBuilder query = new StringBuilder();

            String escapedTable = Formatter.escapeIdentifier(statement.getTable());

            query.append(SqlKeyWord.UPDATE).append(" ")
                    .append(escapedTable).append(" ")
                    .append(SqlKeyWord.SET).append(" ");

            String[] fields = statement.getFields();
            Object[] values = statement.getValues();

            for (int i = 0; i < fields.length - 1; i++) {
                query.append(Formatter.escapeIdentifier(fields[i]))
                        .append(" = ")
                        .append(Formatter.escapeParameter(values[i]))
                        .append(", ");
            }
            query.append(Formatter
                            .escapeIdentifier(fields[fields.length - 1]))
                    .append(" = ")
                    .append(Formatter
                            .escapeParameter(values[values.length - 1]))
                    .append(" ");

            appendWhere(statement, query);

            return query.toString().trim();
        }

        private static void appendLimit(SelectStatement statement, StringBuilder query) {
            Integer limit = statement.getLimit();

            if (limit != null)
                query.append(SqlKeyWord.LIMIT).append(" ")
                        .append(Formatter.escapeParameter(limit))
                        .append(" ");
        }

        private static void appendOrderBy(SelectStatement statement, StringBuilder query) {
            String[] orderBy = statement.getOrderBy();

            if (orderBy != null && orderBy.length != 0) {
                String escapedOrder = Formatter.formatToSequence(orderBy);
                query.append(SqlKeyWord.ORDER_BY).append(" ")
                        .append(escapedOrder).append(" ")
                        .append(statement.getOrderDirection().toString())
                        .append(" ");
            }
        }

        private static void appendWhere(AbstractStatementWhereClauseSupports statement, StringBuilder query) {
            List<LogicalExpression> where = statement.getWhere();

            if (!where.isEmpty()) {
                for (LogicalExpression e : where) {
                    query.append(e.logicalOperator()).append(" ")
                            .append(Formatter
                                    .formatExpression(e.comparison()))
                            .append(" ");
                }
            }
        }
    }
}
