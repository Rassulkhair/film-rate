package com.practice.filmorate.storage.dbImpl;

import com.practice.filmorate.model.Genre;
import com.practice.filmorate.storage.GenreStorage;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;


    private static final String SELECT = """
            SELECT * FROM genres
            """;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(SELECT, this::mapRow);
    }

    @Override
    public Optional<Genre> findById(long genreId) {
        String sql = SELECT + "WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, genreId)
                .stream()
                .findFirst();
    }

    private Genre mapRow(ResultSet rs, int i) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
