package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.exception.ValidationException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long filmId) {
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = %d не найден".formatted(filmId)));
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        findById(film.getId());
        return filmStorage.update(film);
    }

    public void addLike(long filmId, long userId) {
        checkIfUserExists(userId); // проверяет, существует ли пользователь
        Film film = findById(filmId); // проверяет, существует ли фильм
        film.addLike(userId); // добавление лайка, тк лайки хранятся внутри фильма, добавляем в сам фильм
    }

    public void removeLike(long filmId, long userId) {
        // аналогично как в лайке
        checkIfUserExists(userId);
        Film film = findById(filmId);
        film.removeLike(userId);
    }

    public List<Film> findAllPopular(long count) {
        return filmStorage.findAllPopular(count);
    }

    private void checkIfUserExists(long userId) {
        Optional<User> optional = userStorage.findById(userId);
        if (optional.isEmpty()) {
            throw new NotFoundException("Пользователь с id = %d не найден".formatted(userId));
        }
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() != null && film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
