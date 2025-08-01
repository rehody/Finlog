package org.example.finlog.repository;

import org.example.finlog.entity.User;
import org.example.finlog.factory.user.query.UserInsertFactory;
import org.example.finlog.factory.user.query.UserQueryFactory;
import org.example.finlog.factory.user.query.UserSelectFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper =
            new BeanPropertyRowMapper<>(User.class);

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public User getUserByEmail(String email) {
        try {
            String query = UserSelectFactory.getByEmail(email);
            return jdbcTemplate.queryForObject(query, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public LocalDateTime getRegistrationDate(UUID id) {
        String query = UserSelectFactory.getSingleField("registration_date", id);
        return jdbcTemplate.queryForObject(query, LocalDateTime.class);
    }

    @Transactional
    public void save(User user) {
        String query = UserInsertFactory.save(user);
        jdbcTemplate.update(query);
    }
}
