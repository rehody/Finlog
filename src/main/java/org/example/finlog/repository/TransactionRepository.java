package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.util.QueryResponse;
import org.example.finlog.util.TransactionQueryFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Transaction> getFiltered(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(
                "select id, user_id, amount, description, " +
                        "category, transaction_date, deleted, " +
                        "version, created_at, updated_at, deleted_at " +
                        "from transaction_ " +
                        "where user_id = ? and " +
                        "transaction_date between ? and ? " +
                        "and deleted = false " +
                        "order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                userId,
                startDate,
                endDate
        );
    }

    @Transactional
    public List<Transaction> getFiltered(UUID userId, Category category, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(
                "select id, user_id, amount, description, " +
                        "category, transaction_date, deleted, " +
                        "version, created_at, updated_at, deleted_at " +
                        "from transaction_ " +
                        "where user_id = ? and category = ? " +
                        "and transaction_date between ? and ? " +
                        "and deleted = false " +
                        "order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                userId,
                category.toString(),
                startDate,
                endDate
        );
    }

    @Transactional
    public List<Transaction> getAllByUserId(UUID userId) {
        return jdbcTemplate.query(
                "select id, user_id, amount, description, " +
                        "category, transaction_date, deleted, " +
                        "version, created_at, updated_at, deleted_at " +
                        "from transaction_ " +
                        "where user_id = ? " +
                        "and deleted = false " +
                        "order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                userId
        );
    }

    @Transactional
    public void save(Transaction transaction) {
        QueryResponse query = TransactionQueryFactory.createSaveQuery(transaction);

        jdbcTemplate.update(
                query.sql(),
                query.params()
        );
    }

    @Transactional
    public void delete(UUID id, Long version) {
        int affectedRows = jdbcTemplate.update(
                "update transaction_ set deleted = true, " +
                        "deleted_at = now(), version = version + 1 " +
                        "where id = ? " +
                        "and version = ? " +
                        "and deleted = false",
                id,
                version
        );

        if (affectedRows == 0 && existsById(id)) {
            throw new OptimisticLockingFailureException(
                    "Failed to delete transaction " + id
                            + " with version " + version
            );
        }
    }

    @Transactional
    public void update(Transaction transaction) {
        int affectedRows = jdbcTemplate.update(
                "update transaction_ set amount = ?, description = ?, category = ?," +
                        " version = version + 1, updated_at = now() " +
                        "where id = ? and version = ?",
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().toString(),
                transaction.getId(),
                transaction.getVersion()
        );

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
            return jdbcTemplate.queryForObject(
                    "select id, user_id, amount, description, " +
                            "category, transaction_date, deleted, " +
                            "version, created_at, updated_at, deleted_at " +
                            "from transaction_ " +
                            "where id = ? and deleted = false",
                    new BeanPropertyRowMapper<>(Transaction.class),
                    id
            );
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
