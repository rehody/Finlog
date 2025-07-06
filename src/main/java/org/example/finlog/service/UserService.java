package org.example.finlog.service;

import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public LocalDate getRegistrationDate(UUID id) {
        return userRepository.getRegistrationDate(id);
    }
}
