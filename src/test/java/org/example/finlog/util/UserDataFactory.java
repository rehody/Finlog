package org.example.finlog.util;

import org.example.finlog.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDataFactory {

    public static User sampleUser(UUID id) {
        return User.builder()
                .id(id)
                .name("username")
                .email("sample@email.com")
                .passwordHash("password_hash")
                .registrationDate(LocalDateTime.now())
                .softDelete(false)
                .build();
    }

}
