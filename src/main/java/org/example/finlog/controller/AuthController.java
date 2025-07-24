package org.example.finlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.AuthResponse;
import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.service.UserService;
import org.example.finlog.util.ApiTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = ApiTag.AUTHENTICATION, description = "User login and registration")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user and returns JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials")
            })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login attempt: {}", request.getEmail());
        String token = userService.login(request);
        log.info("User logged: {}", request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(
            summary = "User registration",
            description = "Registers new user and returns JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Email already exists")
            })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = userService.register(request);
        log.info("User registered: {}", request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}
