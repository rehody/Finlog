package org.example.finlog.util;

import org.example.finlog.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

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
}

