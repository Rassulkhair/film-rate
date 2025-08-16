package com.practice.filmorate.storage.dbImpl;


import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.FilmStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                     "FROM films f " +
                     "JOIN mpa m ON f.mpa_id = m.id " +
                     "ORDER BY f.id";   // ✅ стабильный порядок по id
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        films.forEach(f -> f.setGenres(loadGenresForFilm(f.getId())));
        return films;
    }

    @Override
    public Optional<Film> findById(long filmId) {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                     "FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, filmId);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        film.setGenres(loadGenresForFilm(film.getId()));
        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        if (!mpaExists(film.getMpa().getId())) {
            throw new NotFoundException("MPA with id=" + film.getMpa().getId() + " not found");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!genreExists(genre.getId())) {
                    throw new NotFoundException("Genre with id=" + genre.getId() + " not found");
                }
            }
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        saveGenresForFilm(film);

        return findById(id).get();
    }

    @Override
    public Film update(Film film) {
        if (findById(film.getId()).isEmpty()) {
            throw new NotFoundException("Film with id=" + film.getId() + " not found");
        }

        if (!mpaExists(film.getMpa().getId())) {
            throw new NotFoundException("MPA with id=" + film.getMpa().getId() + " not found");
        }

        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id=?", film.getId());
        saveGenresForFilm(film);

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public List<Film> findAllPopular(long count) {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                     "FROM films f " +
                     "JOIN mpa m ON f.mpa_id = m.id " +
                     "LEFT JOIN film_likes l ON f.id = l.film_id " +
                     "GROUP BY f.id, m.id " +
                     "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                     "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        films.forEach(f -> f.setGenres(loadGenresForFilm(f.getId())));
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")),
                new HashSet<>()
        );
    }

    private Set<Genre> loadGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM films_genres fg " +
                     "JOIN genres g ON fg.genre_id = g.id " +
                     "WHERE fg.film_id = ? " +
                     "ORDER BY g.id";

        return new LinkedHashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                filmId));
    }

    private void saveGenresForFilm(Film film) {

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        genres.addAll(film.getGenres());

        String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            if (!genreExists(genre.getId())) {
                throw new NotFoundException("Genre with id=" + genre.getId() + " not found");
            }
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }

        film.setGenres(genres);

    }

    private boolean genreExists(long id) {
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) " +
                     "SELECT ?, ? FROM DUAL WHERE NOT EXISTS (" +
                     "    SELECT 1 FROM film_likes WHERE film_id = ? AND user_id = ?" +
                     ")";
        jdbcTemplate.update(sql, filmId, userId, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private boolean mpaExists(long id) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}

