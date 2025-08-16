package com.practice.filmorate.storage.dbImpl;

import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.MpaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;


    private static final String SELECT = """
            SELECT * FROM mpa
            """;

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query(SELECT, this::mapRow);
    }

    @Override
    public Optional<Mpa> findById(long mpaId) {
        String sql = SELECT + "WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, mpaId)
                .stream()
                .findFirst();

    }


    private Mpa mapRow(ResultSet rs, int i) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
