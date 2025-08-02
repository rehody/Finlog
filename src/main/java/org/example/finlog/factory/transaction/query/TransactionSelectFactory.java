package org.example.finlog.factory.transaction.query;

import org.example.finlog.enums.Category;
import org.example.finlog.query_builder.builder.SelectQueryBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionSelectFactory extends TransactionQueryFactory{

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

    public static String getAllByUserId(UUID userId) {
        return SelectQueryBuilder.builder()
                .select()
                .from(TABLE)
                .where("user_id").eq(userId)
                .and("deleted").eq(false)
                .orderBy("transaction_date")
                .build();
    }
}
