package org.example.finlog.util;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionQueryFactory {
    public static QueryResponse createSaveQuery(Transaction transaction) {
        List<Object> params = new ArrayList<>();
        StringBuilder fields = new StringBuilder("id, user_id, amount, description, category");

        params.add(transaction.getId());
        params.add(transaction.getUserId());
        params.add(transaction.getAmount());
        params.add(transaction.getDescription());
        params.add(transaction.getCategory().toString());

        if (transaction.getTransactionDate() != null) {
            params.add(transaction.getTransactionDate());
            fields.append(", transaction_date");
        }


        String query = "insert into transaction_ (" +
                fields + ") values (" +
                "?" + ", ?".repeat(params.size() - 1) + ")";

        return new QueryResponse(query, params.toArray());
    }

    public static QueryResponse createGetFilteredQuery(
            UUID userId,
            Category category,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<Object> params = new ArrayList<>();
        params.add(userId);

        StringBuilder query = new StringBuilder(
                "select id, user_id, amount, description, " +
                        "category, transaction_date, deleted, " +
                        "version, created_at, updated_at, deleted_at " +
                        "from transaction_ " +
                        "where user_id = ? "
        );

        if (category != null) {
            query.append("and category = ? ");
            params.add(category.toString());
        }
        params.add(startDate);
        params.add(endDate);
        query.append(
                "and transaction_date between ? and ? " +
                        "and deleted = false " +
                        "order by transaction_date"
        );

        return new QueryResponse(query.toString(), params.toArray());
    }
}

