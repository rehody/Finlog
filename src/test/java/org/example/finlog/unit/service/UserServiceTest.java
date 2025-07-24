package org.example.finlog.unit.service;

import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.security.JwtService;
import org.example.finlog.service.UserService;
import org.example.finlog.util.UserDataFactory;
import org.example.finlog.util.UuidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = UserDataFactory.sampleUser(userId);
    }

    @Test
    void getUserByEmail_shouldReturnUserIfExists() {
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        Optional<User> result = userService.getUserByEmail(user.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void getUserByEmail_shouldReturnEmptyIfUserNotExists() {
        when(userRepository.getUserByEmail("unknown@example.com")).thenReturn(null);
        Optional<User> result = userService.getUserByEmail("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void getRegistrationDate_shouldReturnRegistrationDateCorrectly() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 8, 0, 0);
        when(userRepository.getRegistrationDate(userId)).thenReturn(date);

        LocalDateTime result = userService.getRegistrationDate(userId);

        assertThat(result).isEqualTo(date);
    }

    @Test
    void register_shouldSaveUserAndReturnToken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("NewUser")
                .email("new@example.com")
                .password("plainPassword")
                .build();

        when(uuidGenerator.generate()).thenReturn(userId);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(jwtService.generateToken("new@example.com")).thenReturn("token");

        String token = userService.register(request);

        verify(userRepository).save(argThat(u ->
                u.getId().equals(userId) &&
                        u.getName().equals("NewUser") &&
                        u.getEmail().equals("new@example.com") &&
                        u.getPasswordHash().equals("encodedPassword")
        ));
        assertThat(token).isEqualTo("token");
    }

    @Test
    void register_shouldUseEmailAsName() {
        RegisterRequest request = RegisterRequest.builder()
                .email("new@example.com")
                .password("pass")
                .build();

        when(uuidGenerator.generate()).thenReturn(userId);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPassword");
        when(jwtService.generateToken("new@example.com")).thenReturn("token");

        userService.register(request);

        verify(userRepository).save(argThat(u ->
                u.getName().equals("new@example.com")
        ));
    }

    @Test
    void login_shouldReturnTokenWhenValidCredentials() {
        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password("plainPassword")
                .build();

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("plainPassword", user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail())).thenReturn("token");

        String token = userService.login(request);

        assertThat(token).isEqualTo("token");
    }

    @Test
    void login_shouldThrowWhenInvalidEmail() {
        LoginRequest request = LoginRequest.builder()
                .email("unknown@example.com")
                .password("pass")
                .build();

        when(userRepository.getUserByEmail("unknown@example.com")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_shouldThrowWhenInvalidPassword() {
        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password("wrongPassword")
                .build();

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
