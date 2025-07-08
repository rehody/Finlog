package org.example.finlog.integration.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.repository.TransactionRepository;
import org.example.finlog.repository.UserRepository;
import org.example.finlog.util.TransactionDataFactory;
import org.example.finlog.util.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Import({TransactionRepository.class, UserRepository.class})
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = UserDataFactory.sampleUser(UUID.randomUUID());
        userRepository.save(user);
    }

    @Test
    void shouldInsertInteraction() {
        UUID transactionId = UUID.randomUUID();

        Transaction transaction = TransactionDataFactory
                .sampleTransaction(transactionId, user);

        transactionRepository.save(user.getId(), transaction);

        List<Transaction> result = transactionRepository.getAllByUserId(user.getId());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(transactionId);
    }

    @Test
    void shouldInsertTransaction() {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(transactionId, user);

        transactionRepository.save(user.getId(), transaction);

        List<Transaction> result = transactionRepository.getAllByUserId(user.getId());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(transactionId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsForUser() {
        UUID randomUserId = UUID.randomUUID();
        List<Transaction> result = transactionRepository.getAllByUserId(randomUserId);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterTransactionsByDateRange() {
        Transaction transaction1 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction1.setTransactionDate(LocalDateTime.of(2025, 7, 1, 12, 0));

        Transaction transaction2 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction2.setTransactionDate(LocalDateTime.of(2025, 12, 10, 11, 11));

        transactionRepository.save(user.getId(), transaction1);
        transactionRepository.save(user.getId(), transaction2);

        List<Transaction> filtered = transactionRepository.getFiltered(
                LocalDateTime.of(2025, 9, 1, 10, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0)
        );

        assertThat(filtered).hasSize(1);
        assertThat(filtered.getFirst().getId()).isEqualTo(transaction2.getId());
    }

    @Test
    void shouldFilterTransactionsByCategoryAndDateRange() {
        Transaction transaction1 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction1.setTransactionDate(LocalDateTime.of(2025, 7, 1, 12, 0));
        transaction1.setCategory(Category.OTHER);

        Transaction transaction2 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction2.setTransactionDate(LocalDateTime.of(2025, 10, 9, 23, 51));
        transaction2.setCategory(Category.FAST_FOOD);

        Transaction transaction3 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction3.setTransactionDate(LocalDateTime.of(2025, 12, 10, 11, 11));
        transaction3.setCategory(Category.OTHER);

        Transaction transaction4 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction4.setTransactionDate(LocalDateTime.of(2025, 11, 12, 13, 14));
        transaction4.setCategory(Category.FAST_FOOD);

        transactionRepository.save(user.getId(), transaction1);
        transactionRepository.save(user.getId(), transaction2);
        transactionRepository.save(user.getId(), transaction3);
        transactionRepository.save(user.getId(), transaction4);

        List<Transaction> filtered = transactionRepository.getFiltered(
                Category.FAST_FOOD,
                LocalDateTime.of(2025, 9, 1, 10, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0)
        );

        assertThat(filtered).hasSize(2);
        assertThat(filtered)
                .extracting(Transaction::getId)
                .containsExactlyInAnyOrder(transaction2.getId(), transaction4.getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsMatchFilters() {
        Transaction transaction = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction.setTransactionDate(LocalDateTime.of(2025, 7, 1, 12, 0));
        transaction.setCategory(Category.OTHER);

        transactionRepository.save(user.getId(), transaction);

        List<Transaction> filtered = transactionRepository.getFiltered(
                Category.FAST_FOOD,
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 0, 0)
        );

        assertThat(filtered).isEmpty();
    }
}
