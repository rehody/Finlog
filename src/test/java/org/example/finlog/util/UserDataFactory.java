package org.example.finlog.util;

import org.example.finlog.DTO.UserRequest;
import org.example.finlog.entity.User;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class UserDataFactory {

    public static User sampleUser(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        int random = new Random().nextInt(1, 1 << 20);
        return User.builder()
                .id(id)
                .name("username")
                .email(random + "@email.com") // to avoid duplicate emails when saving
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
