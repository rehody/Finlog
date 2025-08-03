package org.example.finlog.repository;

import lombok.RequiredArgsConstructor;
import org.example.finlog.entity.BaseEntity;
import org.example.finlog.factory.common.query.BaseQueryFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseRepository<T extends BaseEntity> {
    protected final String table;
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    public abstract void save(T entity);

    public abstract void update(T entity);

    public abstract void delete(UUID id, Long version);


    @Transactional
    public T getById(UUID id) {
        try {
            String query = BaseQueryFactory.getByField(table, "id", id);
            return jdbcTemplate.queryForObject(query, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public List<T> getAll() {
        String query = BaseQueryFactory.getAll(table);
        return jdbcTemplate.query(query, rowMapper);
    }

    @Transactional
    public Long getVersion(UUID id) {
        String query = BaseQueryFactory.getSingleField(table, "version", id);
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    protected void checkOptimisticLock(int affectedRows, UUID id, String message) {
        if (affectedRows == 0 && existsById(id)) {
            throw new OptimisticLockingFailureException(message);
        }
    }

    protected boolean existsById(UUID id) {
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        "select exists(select 1 from " + table + " where id = ?)",
                        Boolean.class,
                        id
                ));
    }
}
