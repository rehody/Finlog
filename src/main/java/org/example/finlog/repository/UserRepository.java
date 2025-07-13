package org.example.finlog.repository;

import org.example.finlog.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public User getUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from user_ where email = ?",
                    new BeanPropertyRowMapper<>(User.class),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public LocalDateTime getRegistrationDate(UUID id) {
        return jdbcTemplate.queryForObject(
                "select registration_date from user_ where id = ?",
                LocalDateTime.class,
                id
        );
    }

    @Transactional
    public void save(User user) {
        jdbcTemplate.update(
                "Insert into user_ (id, name, email, password_hash, registration_date, soft_delete) values (?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRegistrationDate(),
                user.isSoftDelete()
        );
    }
}
