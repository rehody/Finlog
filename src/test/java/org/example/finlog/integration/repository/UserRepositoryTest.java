package org.example.finlog.integration.repository;

import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.util.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@ActiveProfiles("test")
@Import(UserRepository.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserDataFactory.sampleUser(UUID.randomUUID());
    }

    @Test
    void shouldSaveAndRetrieveUserByEmail() {
        userRepository.save(user);

        User retrieved = userRepository.getUserByEmail(user.getEmail());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(user.getId());
        assertThat(retrieved.getEmail()).isEqualTo(user.getEmail());
        assertThat(retrieved.getName()).isEqualTo(user.getName());
    }

    @Test
    void getUserByEmail_shouldReturnNullIfUserNotExists() {
        User result = userRepository.getUserByEmail("notfound@example.com");

        assertThat(result).isNull();
    }

    @Test
    void shouldGetRegistrationDateById() {
        userRepository.save(user);

        LocalDate date = userRepository.getRegistrationDate(user.getId());

        assertThat(date).isEqualTo(user.getRegistrationDate());
    }

    @Test
    void getRegistrationDate_shouldThrowIfUserNotExists() {
        UUID fakeId = UUID.randomUUID();

        assertThatThrownBy(() -> userRepository.getRegistrationDate(fakeId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
