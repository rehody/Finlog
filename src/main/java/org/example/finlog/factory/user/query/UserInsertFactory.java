package org.example.finlog.factory.user.query;

import org.example.finlog.entity.User;
import org.example.finlog.factory.common.query.BaseQueryFactory;
import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.util.TableName;

public class UserInsertFactory extends UserQueryFactory {
    public static String save(User user) {
        String[] fields = {"id", "name", "email", "password_hash", "registration_date"};
        Object[] values = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRegistrationDate()
        };
        return save(TABLE, fields, values);
    }
}
