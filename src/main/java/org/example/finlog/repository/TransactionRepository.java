package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Transaction> getFiltered(LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.queryForList(
                "select * from transaction_ where ? < transaction_date and transaction_date < ?",
                Transaction.class,
                startDate,
                endDate
        );
    }

    @Transactional
    public List<Transaction> getFiltered(Category category, LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.queryForList(
                "select * from transaction_ where category = ? and ? < transaction_date < ?",
                Transaction.class,
                category,
                startDate,
                endDate
        );
    }

    @Transactional
    public void save(UUID userId, Transaction transaction) {
        jdbcTemplate.update(
                "insert into transaction_ (id, user_id, amount, description, category) values (?, ?, ?, ?, ?)",

                transaction.getId(),
                userId,
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().toString()
        );
    }
}
