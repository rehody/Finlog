package org.example.finlog.util;

import org.example.finlog.entity.User;
import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.query_builder.builder.SelectQueryBuilder;

import java.util.UUID;

public class UserQueryFactory {
    private final static String TABLE = TableName.USER;

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

    public static String save(User user) {
        return InsertQueryBuilder.builder()
                .insertInto(TABLE)
                .fields("id", "name", "email", "password_hash", "registration_date")
                .values(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.getRegistrationDate()
                )
                .build();
    }
}
