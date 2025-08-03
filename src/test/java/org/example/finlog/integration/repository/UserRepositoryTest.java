package org.example.finlog.integration.repository;

import org.example.finlog.config.TestPostgresContainerConfig;
import org.example.finlog.entity.User;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.util.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ImportTestcontainers(TestPostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@ActiveProfiles("test")
@Import(UserRepository.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = UserDataFactory.sampleUser(userId);
    }

    @Test
    void getUserByEmail_shouldSaveAndRetrieveUserByEmail() {
        userRepository.save(user);

        User retrieved = userRepository.getUserByEmail(user.getEmail());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(userId);
        assertThat(retrieved.getEmail()).isEqualTo(user.getEmail());
        assertThat(retrieved.getName()).isEqualTo(user.getName());
    }

    @Test
    void getUserByEmail_shouldReturnNullIfUserNotExists() {
        User result = userRepository.getUserByEmail("notfound@example.com");

        assertThat(result).isNull();
    }

    @Test
    void getRegistrationDate_shouldGetRegistrationDateById() {
        userRepository.save(user);

        LocalDateTime date = userRepository.getRegistrationDate(userId);

        assertThat(date)
                .usingComparator(
                        Comparator.comparing((LocalDateTime d) ->
                                d.truncatedTo(ChronoUnit.MILLIS))
                )
                .isEqualTo(user.getRegistrationDate());
    }

    @Test
    void getRegistrationDate_shouldThrowIfUserNotExists() {
        UUID fakeId = UUID.randomUUID();

        assertThatThrownBy(() -> userRepository.getRegistrationDate(fakeId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void update_shouldUpdateUserCorrectly() {
        String email = "sample@email.com";
        String passwordHash = "password_hash";
        LocalDateTime registrationDate = LocalDateTime.of(2025, 8, 3, 18, 50, 0);

        User existing = User.builder()
                .id(userId)
                .name("old name")
                .email(email)
                .passwordHash(passwordHash)
                .registrationDate(registrationDate)
                .build();

        userRepository.save(existing);

        User request = User.builder()
                .id(userId)
                .name("new name")
                .email(email)
                .passwordHash(passwordHash)
                .registrationDate(registrationDate)
                .version(0L)
                .build();

        userRepository.update(request);

        User updated = userRepository.getUserByEmail(existing.getEmail());
        compareUsers(request, updated);
    }

    @Test
    void update_shouldDoNothingWhenUserNotExists() {
        User notExisting = UserDataFactory.sampleUser(UUID.randomUUID());

        userRepository.update(notExisting);

        assertThat(userRepository.getUserByEmail(notExisting.getEmail())).isNull();
    }

    @Test
    void update_shouldThrowWhenVersionMismatch() {
        userRepository.save(user);

        user.setVersion(user.getVersion() + 1);
        assertThatThrownBy(() ->
                userRepository.update(user)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to update user " + user.getId()
                        + " with version " + user.getVersion()
                );
    }

    @Test
    void delete_shouldDeleteUserCorrectly() {
        userRepository.save(user);

        Long version = user.getVersion();
        userRepository.delete(userId, version);

        User deleted = userRepository.getUserByEmail(user.getEmail());
        assertThat(deleted).isNull();
    }

    @Test
    void delete_shouldDoNothingIfUserDoesNotExist() {
        User usr1 = UserDataFactory.sampleUser(UUID.randomUUID());
        User usr2 = UserDataFactory.sampleUser(UUID.randomUUID());

        userRepository.save(usr1);
        userRepository.save(usr2);

        int countBefore = userRepository.getAll().size();
        userRepository.delete(userId, 0L);
        int countAfter = userRepository.getAll().size();

        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    void delete_shouldNotAffectOtherUsers() {
        User usr1 = UserDataFactory.sampleUser(UUID.randomUUID());
        User usr2 = UserDataFactory.sampleUser(UUID.randomUUID());

        userRepository.save(usr1);
        userRepository.save(usr2);

        int countBefore = userRepository.getAll().size();
        userRepository.delete(usr2.getId(), 0L);
        int countAfter = userRepository.getAll().size();

        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(userRepository.getUserByEmail(usr2.getEmail())).isNull();
        compareUsers(userRepository.getUserByEmail(usr1.getEmail()), usr1);
    }


    @Test
    void delete_shouldThrowWhenVersionMismatch() {
        userRepository.save(user);

        Long version = user.getVersion() + 1;
        assertThatThrownBy(() ->
                userRepository.delete(userId, version)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to update user " + userId
                        + " with version " + version
                );
    }

    @Test
    void delete_shouldThrowWhenAlreadyDeleted() {
        userRepository.save(user);

        userRepository.delete(userId, user.getVersion());
        Long version = user.getVersion() + 1;
        assertThatThrownBy(() ->
                userRepository.delete(userId, version)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to update user " + userId
                        + " with version " + version
                );
    }


    private static void compareUsers(User usr1, User usr2) {
        assertThat(usr1)
                .usingRecursiveComparison()
                .ignoringFields("transactions", "createdAt",
                        "updatedAt", "deletedAt", "version")
                .withComparatorForType(
                        Comparator.comparing((LocalDateTime d) -> d.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class
                )
                .isEqualTo(usr2);
    }
}



