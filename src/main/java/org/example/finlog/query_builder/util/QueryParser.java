package org.example.finlog.query_builder.util;

import org.example.finlog.query_builder.statement.expression.LogicalExpression;
import org.example.finlog.query_builder.statement.node.InsertStatement;
import org.example.finlog.query_builder.statement.node.SelectStatement;

import java.util.List;

public class QueryParser {
    public static String parseSelect(SelectStatement statement) {
        StringBuilder query = new StringBuilder();

        String formattedFields = statement.getFields() == null ? "*" :
                QueryFormatter.formatToSequence(statement.getFields());

        String escapedTable = QueryFormatter
                .escapeIdentifier(statement.getTable());

        query.append("SELECT ")
                .append(formattedFields)
                .append(" FROM ")
                .append(escapedTable)
                .append(" ");

        appendWhere(statement, query);
        appendOrderBy(statement, query);
        appendLimit(statement, query);

        return query.toString().trim();
    }

    public static String parseInsert(InsertStatement statement) {
        StringBuilder query = new StringBuilder();

        String formattedFields = QueryFormatter.formatToSequence(statement.getFields());
        String formattedValues = QueryFormatter.formatToSequence(statement.getValues());
        String escapedTable = QueryFormatter.escapeIdentifier(statement.getTable());

        query.append("INSERT INTO ")
                .append(escapedTable)
                .append(" (")
                .append(formattedFields)
                .append(") ")
                .append("VALUES (")
                .append(formattedValues)
                .append(") ");

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
