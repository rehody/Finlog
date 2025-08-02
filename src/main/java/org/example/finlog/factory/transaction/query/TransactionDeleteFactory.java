package org.example.finlog.factory.transaction.query;

import java.util.UUID;

public class TransactionDeleteFactory extends TransactionQueryFactory {

    public static String delete(UUID id, Long version) {
        return delete(TABLE, id, version);
    }
}

