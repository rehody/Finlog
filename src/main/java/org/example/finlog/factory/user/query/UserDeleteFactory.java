package org.example.finlog.factory.user.query;

import java.util.UUID;

public class UserDeleteFactory extends UserQueryFactory {

    public static String delete(UUID id, Long version) {
        return delete(TABLE, id, version);
    }
}
