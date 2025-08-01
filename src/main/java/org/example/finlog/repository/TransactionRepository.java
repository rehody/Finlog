package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.util.TransactionQueryFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Transaction> rowMapper =
            new BeanPropertyRowMapper<>(Transaction.class);

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Transaction> getFiltered(
            UUID userId,
            Category category,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String query = TransactionQueryFactory
                .getFiltered(userId, category, startDate, endDate);

        return jdbcTemplate.query(query, rowMapper);
    }

    @Transactional
    public List<Transaction> getAllByUserId(UUID userId) {
        String query = TransactionQueryFactory.getAllByUserId(userId);
        return jdbcTemplate.query(query, rowMapper);
    }

    @Transactional
    public void save(Transaction transaction) {
        String query = TransactionQueryFactory.save(transaction);
        jdbcTemplate.update(query);
    }

    @Transactional
    public void delete(UUID id, Long version) {
        String query = TransactionQueryFactory.delete(id, version);
        int affectedRows = jdbcTemplate.update(query);

        if (affectedRows == 0 && existsById(id)) {
            throw new OptimisticLockingFailureException(
                    "Failed to delete transaction " + id
                    + " with version " + version
            );
        }
    }

    @Transactional
    public void update(Transaction transaction) {
        String query = TransactionQueryFactory.update(transaction);
        int affectedRows = jdbcTemplate.update(query);

        if (affectedRows == 0 && existsById(transaction.getId())) {
            throw new OptimisticLockingFailureException(
                    "Failed to update transaction " + transaction.getId()
                    + "with version " + transaction.getVersion()
            );
        }
    }

    @Transactional
    public Transaction getById(UUID id) {
        try {
            String query = TransactionQueryFactory.getById(id);
            return jdbcTemplate.queryForObject(query, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private boolean existsById(UUID id) {
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        "select exists(select 1 from transaction_ where id = ?)",
                        Boolean.class,
                        id
                ));
    }
}
