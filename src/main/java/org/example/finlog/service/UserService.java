package org.example.finlog.service;

import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.security.JwtService;
import org.example.finlog.util.UuidGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UuidGenerator uuidGenerator;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UuidGenerator uuidGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.uuidGenerator = uuidGenerator;
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.getUserByEmail(email));
    }

    public LocalDateTime getRegistrationDate(UUID id) {
        return userRepository.getRegistrationDate(id);
    }

    public String register(RegisterRequest request) {
        User user = mapToEntity(request);

        userRepository.save(user);
        return jwtService.generateToken(user.getEmail());
    }

    public String login(LoginRequest request) {
        User user = getUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public User mapToEntity(RegisterRequest request) {
        return User.builder()
                .id(uuidGenerator.generate())
                .name(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .registrationDate(LocalDateTime.now())
                .build();
    }
}
