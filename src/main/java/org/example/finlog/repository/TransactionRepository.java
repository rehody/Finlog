package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.factory.transaction.query.TransactionDeleteFactory;
import org.example.finlog.factory.transaction.query.TransactionInsertFactory;
import org.example.finlog.factory.transaction.query.TransactionSelectFactory;
import org.example.finlog.factory.transaction.query.TransactionUpdateFactory;
import org.example.finlog.util.TableName;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository extends BaseRepository<Transaction> {
    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        super(
                TableName.TRANSACTION,
                jdbcTemplate,
                new BeanPropertyRowMapper<>(Transaction.class)
        );
    }

    @Transactional
    public List<Transaction> getFiltered(
            UUID userId,
            Category category,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String query = TransactionSelectFactory
                .getFiltered(userId, category, startDate, endDate);

        return jdbcTemplate.query(query, rowMapper);
    }

    @Transactional
    public List<Transaction> getAllByUserId(UUID userId) {
        String query = TransactionSelectFactory.getAllByUserId(userId);
        return jdbcTemplate.query(query, rowMapper);
    }

    @Override
    @Transactional
    public void save(Transaction transaction) {
        String query = TransactionInsertFactory.save(transaction);
        jdbcTemplate.update(query);
    }

    @Override
    @Transactional
    public void delete(UUID id, Long version) {
        String query = TransactionDeleteFactory.delete(id, version);
        int affectedRows = jdbcTemplate.update(query);

        checkOptimisticLock(
                affectedRows, id,
                "Failed to delete transaction " + id
                + " with version " + version
        );
    }

    @Override
    @Transactional
    public void update(Transaction transaction) {
        String query = TransactionUpdateFactory.update(transaction);
        int affectedRows = jdbcTemplate.update(query);

        checkOptimisticLock(
                affectedRows,
                transaction.getId(),
                "Failed to update transaction " + transaction.getId()
                + " with version " + transaction.getVersion()
        );
    }
}
