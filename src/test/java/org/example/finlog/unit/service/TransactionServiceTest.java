package org.example.finlog.unit.service;


import jakarta.persistence.EntityNotFoundException;
import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.repository.TransactionRepository;
import org.example.finlog.service.TransactionService;
import org.example.finlog.service.UserService;
import org.example.finlog.util.TransactionDataFactory;
import org.example.finlog.util.UserDataFactory;
import org.example.finlog.util.UuidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private TransactionService transactionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserDataFactory.sampleUser(UUID.randomUUID());
    }

    @Test
    void getFilteredData_shouldReturnFiltered() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0);
        Category category = Category.FAST_FOOD;

        List<Transaction> expected = List.of(new Transaction());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(start);
        when(transactionRepository.getFiltered(user.getId(), category, start, end)).thenReturn(expected);

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), category, start, end);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getFilteredData_shouldReturnFilteredWhenCategoryIsNull() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0);

        List<Transaction> expected = List.of(new Transaction());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(start);
        when(transactionRepository.getFiltered(user.getId(), start, end)).thenReturn(expected);

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), null, start, end);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getFilteredData_shouldUseDefaultValuesWhenDateIsNull() {
        LocalDateTime regDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(regDate);
        when(transactionRepository.getFiltered(any(), any(), any())).thenReturn(List.of());

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), null, null, null);

        assertThat(result).isNotNull();
    }

    @Test
    void getFilteredData_shouldThrowsWhenUserNotFound() {
        when(userService.getUserByEmail("nope@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.getFilteredData("nope@example.com", null, null, null)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void save_shouldCreateAndSaveTransactionCorrectly() {
        UUID txId = UUID.randomUUID();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(uuidGenerator.generate()).thenReturn(txId);

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .description("Lunch")
                .category(Category.FAST_FOOD)
                .build();


        transactionService.save(user.getEmail(), request);

        verify(transactionRepository).saveWithoutDate(eq(user.getId()), argThat(tx ->
                tx.getId().equals(txId)
                        && tx.getAmount().equals(BigDecimal.valueOf(100))
                        && tx.getDescription().equals("Lunch")
                        && tx.getCategory() == Category.FAST_FOOD
                        && tx.getUser().equals(user)
        ));
    }

    @Test
    void update_shouldUpdateTransactionCorrectly() throws AccessDeniedException {
        UUID txId = UUID.randomUUID();

        Transaction existing = Transaction.builder()
                .id(txId)
                .user(user)
                .userId(user.getId())
                .amount(BigDecimal.valueOf(100))
                .description("old desc")
                .category(Category.OTHER)
                .transactionDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();


        BigDecimal expectedAmount = BigDecimal.valueOf(200);
        String expectedDescription = "new desc";
        Category expectedCategory = Category.FAST_FOOD;
        LocalDateTime expectedTransactionDate = LocalDateTime.of(2026, 1, 1, 0, 0);

        TransactionRequest request = TransactionRequest.builder()
                .id(txId)
                .amount(expectedAmount)
                .description(expectedDescription)
                .category(expectedCategory)
                .transactionDate(expectedTransactionDate)
                .build();


        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionRepository.getById(txId)).thenReturn(existing);

        transactionService.update(user.getEmail(), request);

        verify(transactionRepository).getById(txId);

        verify(transactionRepository).update(argThat(tx ->
                tx.getId().equals(txId) &&
                        tx.getUser().equals(user) &&
                        tx.getAmount().equals(expectedAmount) &&
                        tx.getDescription().equals(expectedDescription) &&
                        tx.getCategory().equals(expectedCategory) &&
                        tx.getTransactionDate().equals(expectedTransactionDate)
        ));

        verify(transactionRepository, never()).delete(any());
        verify(transactionRepository, never()).saveWithoutDate(any(), any());
    }

    @Test
    void update_shouldThrowsWhenUserNotOwner() {
        UUID txId = UUID.randomUUID();

        User attacker = User.builder()
                .id(UUID.randomUUID())
                .email("attacker@example.com")
                .name("attacker")
                .passwordHash("pass")
                .build();


        Transaction existing = Transaction.builder()
                .id(txId)
                .user(user)
                .userId(user.getId())
                .amount(BigDecimal.valueOf(100))
                .description("old desc")
                .category(Category.OTHER)
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .id(txId)
                .amount(BigDecimal.valueOf(200))
                .description("new desc")
                .category(Category.FAST_FOOD)
                .build();


        when(userService.getUserByEmail(attacker.getEmail())).thenReturn(Optional.of(attacker));
        when(transactionRepository.getById(txId)).thenReturn(existing);

        assertThatThrownBy(() ->
                transactionService.update(attacker.getEmail(), request)
        ).isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied");

        verify(transactionRepository, never()).update(any());
    }

    @Test
    void update_shouldThrowsWhenTransactionNotFound() {
        UUID txId = UUID.randomUUID();
        TransactionRequest request = TransactionDataFactory.sampleTransactionRequest(txId);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        doReturn(null).when(transactionRepository).getById(txId);

        assertThatThrownBy(() ->
                transactionService.update(user.getEmail(), request)
        ).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Transaction not found");

        verify(transactionRepository, never()).update(any());
    }

    @Test
    void update_shouldThrowsWhenUserNotFound() {
        UUID txId = UUID.randomUUID();
        TransactionRequest request = TransactionDataFactory.sampleTransactionRequest(txId);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.update(user.getEmail(), request)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: " + user.getEmail());
    }

    @Test
    void delete_shouldDeleteTransactionCorrectly() throws AccessDeniedException {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionRepository.getById(txId)).thenReturn(transaction);

        transactionService.delete(user.getEmail(), txId);

        verify(transactionRepository).delete(eq(txId));

        verify(transactionRepository, never()).update(any());
        verify(transactionRepository, never()).saveWithoutDate(any(), any());
    }

    @Test
    void delete_shouldThrowsWhenUserNotOwner() {
        UUID txId = UUID.randomUUID();

        User attacker = User.builder()
                .id(UUID.randomUUID())
                .email("attacker@example.com")
                .name("attacker")
                .passwordHash("pass")
                .build();


        Transaction transaction = Transaction.builder()
                .id(txId)
                .user(user)
                .userId(user.getId())
                .amount(BigDecimal.valueOf(100))
                .description("old desc")
                .category(Category.OTHER)
                .build();


        when(userService.getUserByEmail(attacker.getEmail())).thenReturn(Optional.of(attacker));
        when(transactionRepository.getById(txId)).thenReturn(transaction);

        assertThatThrownBy(() ->
                transactionService.delete(attacker.getEmail(), txId)
        ).isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied");

        verify(transactionRepository, never()).delete(any());
    }


    @Test
    void delete_shouldThrowsWhenTransactionNotFound() {
        UUID txId = UUID.randomUUID();

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        doReturn(null).when(transactionRepository).getById(txId);

        assertThatThrownBy(() ->
                transactionService.delete(user.getEmail(), txId)
        ).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }

    @Test
    void delete_shouldThrowsWhenUserNotFound() {
        UUID txId = UUID.randomUUID();

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.delete(user.getEmail(), txId)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: " + user.getEmail());
    }
}
