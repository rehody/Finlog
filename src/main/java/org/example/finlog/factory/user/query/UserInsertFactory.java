package org.example.finlog.factory.user.query;

import org.example.finlog.entity.User;
import org.example.finlog.query_builder.builder.InsertQueryBuilder;

public class UserInsertFactory extends UserQueryFactory {

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
