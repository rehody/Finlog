package org.example.finlog.integration.repository;

import org.example.finlog.config.TestPostgresContainerConfig;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ImportTestcontainers(TestPostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    void save_shouldInsertTransactionCorrectly() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);

        transactionRepository.save(transaction);

        List<Transaction> result = transactionRepository.getAllByUserId(user.getId());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(txId);
    }

    @Test
    void getAllByUserId_shouldReturnEmptyListWhenNoTransactionsForUser() {
        UUID randomUserId = UUID.randomUUID();
        List<Transaction> result = transactionRepository.getAllByUserId(randomUserId);
        assertThat(result).isEmpty();
    }

    @Test
    void getById_shouldReturnTransactionCorrectly() {
        UUID txId = UUID.randomUUID();
        Transaction expected = TransactionDataFactory.sampleTransaction(txId, user);

        transactionRepository.save(expected);

        Transaction actual = transactionRepository.getById(txId);

        compareTransactions(actual, expected);
    }

    @Test
    void getFiltered_shouldFilterTransactionsByDateRange() {
        Transaction transaction1 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction1.setTransactionDate(LocalDateTime.of(2025, 7, 1, 12, 0));

        Transaction transaction2 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction2.setTransactionDate(LocalDateTime.of(2025, 12, 10, 11, 11));

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        System.out.println(transactionRepository.getById(transaction2.getId()));

        List<Transaction> filtered = transactionRepository.getFiltered(
                user.getId(),
                null,
                LocalDateTime.of(2025, 9, 1, 10, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0)
        );

        assertThat(filtered).hasSize(1);
        compareTransactions(filtered.getFirst(), transaction2);
    }

    @Test
    void getFiltered_shouldFilterTransactionsByCategoryAndDateRange() {
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

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        List<Transaction> filtered = transactionRepository.getFiltered(
                user.getId(),
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
    void getFiltered_shouldReturnEmptyListWhenNoTransactionsMatchFilters() {
        Transaction transaction = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        transaction.setTransactionDate(LocalDateTime.of(2025, 7, 1, 12, 0));
        transaction.setCategory(Category.OTHER);

        transactionRepository.save(transaction);

        List<Transaction> filtered = transactionRepository.getFiltered(
                user.getId(),
                Category.FAST_FOOD,
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 0, 0)
        );

        assertThat(filtered).isEmpty();
    }

    @Test
    void update_shouldUpdateTransactionCorrectly() {
        UUID txId = UUID.randomUUID();
        LocalDateTime transactionDate = LocalDateTime.now();

        Transaction existing = Transaction.builder()
                .id(txId)
                .user(user)
                .userId(user.getId())
                .amount(BigDecimal.valueOf(100))
                .description("old desc")
                .category(Category.OTHER)
                .transactionDate(transactionDate)
                .build();

        transactionRepository.save(existing);


        Transaction request = Transaction.builder()
                .id(txId)
                .user(user)
                .userId(user.getId())
                .amount(BigDecimal.valueOf(200))
                .description("new desc")
                .category(Category.FAST_FOOD)
                .transactionDate(transactionDate)
                .version(0L)
                .build();

        transactionRepository.update(request);

        Transaction updated = transactionRepository.getById(txId);

        compareTransactions(updated, request);
    }

    @Test
    void update_shouldDoNothingWhenTransactionDoesNotExist() {
        Transaction nonExisting = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);

        int countBefore = transactionRepository.getAllByUserId(user.getId()).size();

        transactionRepository.update(nonExisting);

        int countAfter = transactionRepository.getAllByUserId(user.getId()).size();

        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    void update_shouldThrowWhenVersionMismatch() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);
        transactionRepository.save(transaction);

        transaction.setVersion(transaction.getVersion() + 1);

        assertThatThrownBy(() ->
                transactionRepository.update(transaction)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to update transaction " + transaction.getId()
                                + " with version " + transaction.getVersion()
                );
    }

    @Test
    void delete_shouldDeleteTransactionCorrectly() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);

        transactionRepository.save(transaction);

        transactionRepository.delete(txId, 0L);

        assertThat(transactionRepository.getById(txId)).isEqualTo(null);
    }


    @Test
    void delete_shouldDoNothingWhenTransactionDoesNotExist() {
        UUID txId = UUID.randomUUID();

        int countBefore = transactionRepository.getAllByUserId(user.getId()).size();

        transactionRepository.delete(txId, 0L);

        int countAfter = transactionRepository.getAllByUserId(user.getId()).size();

        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    void delete_shouldNotAffectOtherTransaction() {
        Transaction tx1 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);
        Transaction tx2 = TransactionDataFactory.sampleTransaction(UUID.randomUUID(), user);

        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        int countBefore = transactionRepository.getAllByUserId(user.getId()).size();

        transactionRepository.delete(tx2.getId(), 0L);

        int countAfter = transactionRepository.getAllByUserId(user.getId()).size();

        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(transactionRepository.getById(tx2.getId())).isEqualTo(null);
        compareTransactions(transactionRepository.getById(tx1.getId()), tx1);
    }

    @Test
    void delete_shouldSoftDeleteAndIncrementVersion() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);
        transactionRepository.save(transaction);

        Long version = transaction.getVersion();

        transactionRepository.delete(txId, version);

        Transaction deleted = transactionRepository.getById(txId);
        assertThat(deleted).isNull();
    }

    @Test
    void delete_shouldThrowWhenVersionMismatch() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);
        transactionRepository.save(transaction);

        assertThatThrownBy(() ->
                transactionRepository.delete(txId, transaction.getVersion() + 1)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to delete transaction " + txId
                                + " with version " + (transaction.getVersion() + 1)
                );
    }

    @Test
    void delete_shouldThrowWhenAlreadyDeleted() {
        UUID txId = UUID.randomUUID();
        Transaction transaction = TransactionDataFactory.sampleTransaction(txId, user);
        transactionRepository.save(transaction);

        Long version = transaction.getVersion();

        transactionRepository.delete(txId, version);

        assertThatThrownBy(() ->
                transactionRepository.delete(txId, version + 1)
        ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining(
                        "Failed to delete transaction " + txId
                                + " with version " + (transaction.getVersion() + 1)
                );
    }

    private static void compareTransactions(Transaction tx1, Transaction tx2) {
        assertThat(tx1)
                .usingRecursiveComparison()
                .ignoringFields("user", "createdAt",
                        "updatedAt", "deletedAt", "version")
                .withComparatorForType(
                        Comparator.comparing((LocalDateTime d) -> d.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class
                )
                .withComparatorForType(
                        BigDecimal::compareTo,
                        BigDecimal.class
                )
                .isEqualTo(tx2);
    }
}
