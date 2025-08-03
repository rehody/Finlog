package org.example.finlog.service;

import lombok.RequiredArgsConstructor;
import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.DTO.UserRequest;
import org.example.finlog.entity.User;
import org.example.finlog.exception.UserAlreadyExistsException;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.security.JwtService;
import org.example.finlog.util.UserMapper;
import org.example.finlog.util.UuidGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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

    public void update(String email, UserRequest request) throws AccessDeniedException {
        checkPermission(email, request.getId());

        Long version = userRepository.getVersion(request.getId());
        User user = UserMapper.mapToEntity(request, version);
        userRepository.update(user);
    }

    public void delete(String email, UUID id) throws AccessDeniedException {
        checkPermission(email, id);

        Long version = userRepository.getVersion(id);
        userRepository.delete(id, version);
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

    private void checkPermission(String email, UUID id) throws AccessDeniedException {
        if (userRepository.getUserByEmail(email).getId() != id) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
