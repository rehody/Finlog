package org.example.finlog.util;

import org.example.finlog.DTO.UserRequest;
import org.example.finlog.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDataFactory {

    public static User sampleUser(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .id(id)
                .name("username")
                .email("sample@email.com")
                .passwordHash("password_hash")
                .registrationDate(now)
                .deleted(false)
                .version(0L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static UserRequest sampleUserRequest(UUID id) {
        return UserRequest.builder()
                .id(id)
                .name("username")
                .build();
    }

}
