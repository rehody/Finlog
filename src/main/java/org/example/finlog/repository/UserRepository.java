package org.example.finlog.repository;

import org.example.finlog.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "select * from user_ where email = ?",
                User.class,
                email
        );
    }

    public LocalDate getRegistrationDate(UUID id) {
        return jdbcTemplate.queryForObject(
                "select registration_date from user_ where id = ?",
                LocalDate.class,
                id
        );
    }
}
