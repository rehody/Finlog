package org.example.finlog.unit.service;

import org.example.finlog.DTO.LoginRequest;
import org.example.finlog.DTO.RegisterRequest;
import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.security.JwtService;
import org.example.finlog.service.UserService;
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
        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .name("Test User")
                .build();
    }

    @Test
    void getUserByEmail_returnsUserIfExists() {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void getUserByEmail_returnsEmptyIfNotExists() {
        when(userRepository.getUserByEmail("unknown@example.com")).thenReturn(null);

        Optional<User> result = userService.getUserByEmail("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void getRegistrationDate_returnsDateFromRepository() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 8, 0, 0);
        when(userRepository.getRegistrationDate(userId)).thenReturn(date);

        LocalDateTime result = userService.getRegistrationDate(userId);

        assertThat(result).isEqualTo(date);
    }

    @Test
    void register_savesUserAndReturnsToken() {
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
    void login_withValidCredentials_returnsToken() {
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
    void login_withInvalidEmail_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("pass");

        when(userRepository.getUserByEmail("unknown@example.com")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_withInvalidPassword_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("wrongPassword");

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
