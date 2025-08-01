package org.example.finlog.factory.transaction.query;

import org.example.finlog.entity.Transaction;
import org.example.finlog.query_builder.builder.UpdateQueryBuilder;

import static org.example.finlog.query_builder.util.RawExpression.raw;

public class TransactionUpdateFactory extends TransactionQueryFactory{

    public static String update(Transaction transaction) {
        return UpdateQueryBuilder.builder()
                .update(TABLE)
                .set("amount", "description", "category", "version", "updated_at")
                .values(
                        transaction.getAmount(),
                        transaction.getDescription(),
                        transaction.getCategory().toString(),
                        raw("version + 1"),
                        raw("NOW()")
                )
                .where("id").eq(transaction.getId())
                .and("version").eq(transaction.getVersion())
                .build();
    }
}
