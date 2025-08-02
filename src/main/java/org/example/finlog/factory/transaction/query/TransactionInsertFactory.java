package org.example.finlog.factory.transaction.query;

import org.example.finlog.entity.Transaction;
import org.example.finlog.query_builder.builder.InsertQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class TransactionInsertFactory extends TransactionQueryFactory {

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

        return save(
                TABLE,
                fields.toArray(new String[0]),
                values.toArray()
        );
    }
}
