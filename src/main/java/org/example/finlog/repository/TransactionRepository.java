package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.springframework.dao.EmptyResultDataAccessException;
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
    public List<Transaction> getFiltered(LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(
                "select * from transaction_ where transaction_date " +
                        "between ? and ? order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                startDate,
                endDate
        );
    }

    @Transactional
    public List<Transaction> getFiltered(Category category, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(
                "select * from transaction_ where category = ? " +
                        "and transaction_date between ? and ? order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                category.toString(),
                startDate,
                endDate
        );
    }


    @Transactional
    public List<Transaction> getAllByUserId(UUID userId) {
        return jdbcTemplate.query(
                "select * from transaction_ where user_id = ? order by transaction_date",
                new BeanPropertyRowMapper<>(Transaction.class),
                userId
        );
    }

    @Transactional
    public void save(UUID userId, Transaction transaction) {
        jdbcTemplate.update(
                "insert into transaction_ (id, user_id, amount, description, " +
                        "category, transaction_date) values (?, ?, ?, ?, ?, ?)",
                transaction.getId(),
                userId,
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().toString(),
                transaction.getTransactionDate()
        );
    }

    @Transactional
    public void delete(UUID id) {
        jdbcTemplate.update(
                "delete from transaction_ where id = ?",
                id
        );
    }

    @Transactional
    public void update(Transaction transaction) {
        jdbcTemplate.update(
                "update transaction_ set amount = ?, description = ?, category = ? where id = ?",
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().toString(),
                transaction.getId()
        );
    }

    @Transactional
    public Transaction getById(UUID id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from transaction_ where id = ?",
                    new BeanPropertyRowMapper<>(Transaction.class),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
