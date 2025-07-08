package org.example.finlog.unit.service;


import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.repository.TransactionRepository;
import org.example.finlog.service.TransactionService;
import org.example.finlog.service.UserService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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
    void getFilteredData_withoutCategory_returnsAllFiltered() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0);

        List<Transaction> expected = List.of(new Transaction());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(start.toLocalDate());
        when(transactionRepository.getFiltered(start, end)).thenReturn(expected);

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), null, start, end);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getFilteredData_withCategory_returnsFilteredByCategory() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0);
        Category category = Category.FAST_FOOD;

        List<Transaction> expected = List.of(new Transaction());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(start.toLocalDate());
        when(transactionRepository.getFiltered(category, start, end)).thenReturn(expected);

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), category, start, end);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getFilteredData_withNullDates_usesDefaults() {
        LocalDateTime regDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegistrationDate(user.getId())).thenReturn(regDate.toLocalDate());
        when(transactionRepository.getFiltered(any(), any())).thenReturn(List.of());

        List<Transaction> result = transactionService.getFilteredData(user.getEmail(), null, null, null);

        assertThat(result).isNotNull();
    }

    @Test
    void save_createsAndSavesTransaction() {
        UUID txId = UUID.randomUUID();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(uuidGenerator.generate()).thenReturn(txId);

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .description("Lunch")
                .category(Category.FAST_FOOD)
                .build();


        transactionService.save(user.getEmail(), request);

        verify(transactionRepository).save(eq(user.getId()), argThat(tx ->
                tx.getId().equals(txId)
                        && tx.getAmount().equals(BigDecimal.valueOf(100))
                        && tx.getDescription().equals("Lunch")
                        && tx.getCategory() == Category.FAST_FOOD
                        && tx.getUser().equals(user)
        ));
    }

    @Test
    void getFilteredData_userNotFound_throws() {
        when(userService.getUserByEmail("nope@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.getFilteredData("nope@example.com", null, null, null)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
