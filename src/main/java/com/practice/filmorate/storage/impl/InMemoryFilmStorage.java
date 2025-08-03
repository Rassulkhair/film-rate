package com.practice.filmorate.storage.impl;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.storage.FilmStorage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 0;

    @Override
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> findById(long filmId) {
        return Optional.ofNullable(films.get(filmId)); // возвращаем Optional в котором может отсутстовать объект
    }

    @Override
    public Film create(Film film) {
        film.setId(++nextId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAllPopular(long count) {
        return films.values().stream()
                // сортирока в обратном порядке по кол-во лайков
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count) //лимит до count либо до кол-во фильмов
                .toList();
    }
}
