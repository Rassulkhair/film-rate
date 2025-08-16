package com.practice.filmorate.storage;

import com.practice.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Optional<Film> findById(long filmId);

    Film create(Film film);

    Film update(Film film);

    List<Film> findAllPopular(long count);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
