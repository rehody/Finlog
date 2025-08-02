package org.example.finlog.repository;

import org.example.finlog.entity.User;
import org.example.finlog.factory.user.query.UserDeleteFactory;
import org.example.finlog.factory.user.query.UserInsertFactory;
import org.example.finlog.factory.user.query.UserSelectFactory;
import org.example.finlog.factory.user.query.UserUpdateFactory;
import org.example.finlog.util.TableName;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class UserRepository extends BaseRepository<User> {
    public UserRepository(JdbcTemplate jdbcTemplate) {
        super(
                TableName.USER,
                jdbcTemplate,
                new BeanPropertyRowMapper<>(User.class)
        );
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

    @Override
    @Transactional
    public void save(User user) {
        String query = UserInsertFactory.save(user);
        jdbcTemplate.update(query);
    }

    @Override
    @Transactional
    public void update(User user) {
        String query = UserUpdateFactory.update(user);
        int affectedRows = jdbcTemplate.update(query);

        checkOptimisticLock(
                affectedRows,
                user.getId(),
                "Failed to update user " + user.getId()
                + " with version " + user.getVersion()
        );
    }

    @Override
    @Transactional
    public void delete(UUID id, Long version) {
        String query = UserDeleteFactory.delete(id, version);
        int affectedRows = jdbcTemplate.update(query);

        checkOptimisticLock(
                affectedRows, id,
                "Failed to update user " + id
                + " with version " + version
        );
    }
}
