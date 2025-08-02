package org.example.finlog.factory.user.query;

import org.example.finlog.entity.User;

public class UserUpdateFactory extends UserQueryFactory {

    public static String update(User user) {
        return update(
                TABLE,
                user.getId(),
                user.getVersion(),
                new String[]{"name"},
                new Object[]{user.getName()}
        );
    }
}
