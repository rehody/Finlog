package org.example.finlog.factory.user.query;

import org.example.finlog.query_builder.builder.SelectQueryBuilder;

import java.util.UUID;

public class UserSelectFactory extends UserQueryFactory {

    public static String getByEmail(String email) {
        return SelectQueryBuilder.builder()
                .select()
                .from(TABLE)
                .where("email").eq(email)
                .and("deleted").eq(false)
                .build();
    }

    public static String getSingleField(String field, UUID id) {
        return SelectQueryBuilder.builder()
                .select(field)
                .from(TABLE)
                .where("id").eq(id)
                .and("deleted").eq(false)
                .build();
    }
}
