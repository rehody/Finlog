package org.example.finlog.factory.transaction.query;

import org.example.finlog.entity.Transaction;

public class TransactionUpdateFactory extends TransactionQueryFactory {

    public static String update(Transaction transaction) {
        return update(
                TABLE,
                transaction.getId(),
                transaction.getVersion(),
                new String[]{"amount", "description", "category"},
                new Object[]{
                        transaction.getAmount(),
                        transaction.getDescription(),
                        transaction.getCategory().toString()
                }
        );
    }
}
