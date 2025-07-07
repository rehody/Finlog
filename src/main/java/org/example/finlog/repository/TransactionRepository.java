package org.example.finlog.repository;

import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> getFiltered(LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.queryForList(
                "select * from transaction where ? < transaction_date and transaction_date < ?",
                Transaction.class,
                startDate,
                endDate
        );
    }

    public List<Transaction> getFiltered(Category category, LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.queryForList(
                "select * from transaction where category = ? and ? < transaction_date < ?",
                Transaction.class,
                category,
                startDate,
                endDate
        );
    }

}
