package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.ast.expression.LogicalExpression;
import org.example.finlog.query_builder.ast.node.SelectStatement;

import java.util.Arrays;
import java.util.List;

public class QueryParser {
    public static String parseSelect(SelectStatement statement) {
        StringBuilder query = new StringBuilder();

        List<String> escapedFields = Arrays.stream(statement.getFields())
                .map(QueryFormatter::escapeIdentifier)
                .toList();

        String escapedTable = QueryFormatter
                .escapeIdentifier(statement.getTable());

        query.append("SELECT ")
                .append(String.join(", ", escapedFields))
                .append(" FROM ")
                .append(escapedTable)
                .append(" ");

        appendWhere(statement, query);
        appendOrderBy(statement, query);
        appendLimit(statement, query);

        return query.toString().trim();
    }

    private static void appendLimit(SelectStatement statement, StringBuilder query) {
        Integer limit = statement.getLimit();

        if (limit != null)
            query.append("LIMIT ")
                    .append(QueryFormatter.escapeParameter(limit))
                    .append(" ");
    }

    private static void appendOrderBy(SelectStatement statement, StringBuilder query) {
        String orderBy = statement.getOrderBy();

        if (orderBy != null && !orderBy.isEmpty())
            query.append("ORDER BY ")
                    .append(QueryFormatter.escapeIdentifier(orderBy))
                    .append(" ");
    }

    private static void appendWhere(SelectStatement statement, StringBuilder query) {
        List<LogicalExpression> where = statement.getWhere();

        if (!where.isEmpty()) {
            for (LogicalExpression e : where) {
                query.append(e.logicalOperator()).append(" ")
                        .append(QueryFormatter.formatExpression(e.comparison()))
                        .append(" ");
            }
        }
    }
}
