package org.example.finlog.factory.transaction.query;

import org.example.finlog.query_builder.builder.UpdateQueryBuilder;

import java.util.UUID;

import static org.example.finlog.query_builder.util.RawExpression.raw;

public class TransactionDeleteFactory extends TransactionQueryFactory {

    public static String delete(UUID id, Long version) {
        return UpdateQueryBuilder.builder()
                .update(TABLE)
                .set("deleted", "deleted_at", "version")
                .values(
                        true,
                        raw("NOW()"),
                        raw("version + 1")
                )
                .where("id").eq(id)
                .and("deleted").eq(false)
                .and("version").eq(version)
                .build();
    }
}

