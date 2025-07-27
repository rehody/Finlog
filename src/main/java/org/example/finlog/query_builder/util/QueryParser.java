package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.ast.node.SelectStatement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        if (!statement.getWhere().isEmpty()) {
            query.append("WHERE ").append(statement.getWhere().stream()
                    .map(QueryFormatter::formatExpression)
                    .collect(Collectors.joining(" AND ")))
                    .append(" ");
        }

        if (!statement.getOrderBy().isEmpty())
            query.append("ORDER BY ").append(statement.getOrderBy()).append(" ");

        if (statement.getLimit() != null)
            query.append("LIMIT ").append(statement.getLimit()).append(" ");

        return query.toString().trim();
    }
}
