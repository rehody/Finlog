package org.example.finlog.util;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.query_builder.builder.SelectQueryBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionQueryFactory {
    private final static String TABLE = TableName.TRANSACTION;

    public static String save(Transaction transaction) {
        List<String> fields = new ArrayList<>(List.of(
                "id", "user_id", "amount", "description", "category"
        ));

        List<Object> values = new ArrayList<>(List.of(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().toString()
        ));

        if (transaction.getTransactionDate() != null) {
            fields.add("transaction_date");
            values.add(transaction.getTransactionDate());
        }

        return InsertQueryBuilder.builder()
                .insertInto(TABLE)
                .fields(fields.toArray(new String[0]))
                .values(values.toArray())
                .build();
    }

    public static String getFiltered(
            UUID userId,
            Category category,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        var query = SelectQueryBuilder.builder()
                .select()
                .from(TABLE)
                .where("user_id").eq(userId)
                .and("transaction_date").between(startDate, endDate);

        if (category != null) {
            query.and("category").eq(category);
        }

        return query.orderBy("transaction_date").build();
    }
}

