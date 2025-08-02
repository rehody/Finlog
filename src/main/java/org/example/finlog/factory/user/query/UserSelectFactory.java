package org.example.finlog.factory.user.query;

import java.util.UUID;

public class UserSelectFactory extends UserQueryFactory {

    public static String getByEmail(String email) {
        return getByField(TABLE, "email", email);
    }

    public static String getSingleField(String field, UUID id) {
        return getSingleField(TABLE, field, id);
    }
}
