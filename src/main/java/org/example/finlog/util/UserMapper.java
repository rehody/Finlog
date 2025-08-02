package org.example.finlog.util;

import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.DTO.UserRequest;
import org.example.finlog.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;


public class UserMapper {

    public static User mapToEntity(UserRequest request) {
        return User.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
    }

    public static User mapToEntity(RegisterRequest request, UUID id, String encodedPassword) {
        return User.builder()
                .id(id)
                .name(request.getUsername())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .registrationDate(LocalDateTime.now())
                .build();
    }
}
