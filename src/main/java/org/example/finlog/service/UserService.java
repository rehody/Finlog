package org.example.finlog.service;

import lombok.RequiredArgsConstructor;
import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.DTO.UserRequest;
import org.example.finlog.entity.User;
import org.example.finlog.exception.NotFoundException;
import org.example.finlog.exception.UserAlreadyExistsException;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.security.JwtService;
import org.example.finlog.util.UserMapper;
import org.example.finlog.util.UuidGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UuidGenerator uuidGenerator;

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.getUserByEmail(email));
    }

    public LocalDateTime getRegistrationDate(UUID id) {
        return userRepository.getRegistrationDate(id);
    }

    public void update(UserRequest request) {
        User user = UserMapper.mapToEntity(request);
        userRepository.update(user);
    }

    public void delete(UUID id) {
        User existing = getUser(id);
        userRepository.delete(id, existing.getVersion());
    }

    public String register(RegisterRequest request) {
        String email = request.getEmail();
        if (getUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email '" + email + "' already exists"
            );
        }

        if (request.getUsername() == null) {
            request.setUsername(email);
        }

        User user = UserMapper.mapToEntity(
                request,
                uuidGenerator.generate(),
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);
        return jwtService.generateToken(email);
    }

    public String login(LoginRequest request) {
        String email = request.getEmail();
        User user = getUserByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(email);
    }

    private User getUser(UUID id) {
        return Optional.ofNullable(userRepository.getById(id))
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
