package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.exception.ValidationException;
import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.FilmStorage;
import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final @Qualifier("filmDbStorage") FilmStorage filmStorage;
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

        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Фильм должен содержать рейтинг MPA");
        }

        // только id → имя подтянется при возврате из БД
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        findById(film.getId()); // проверяем, что фильм существует
        return filmStorage.update(film);
    }

    public void addLike(long filmId, long userId) {
        checkIfUserExists(userId);
        findById(filmId); // проверим, что фильм существует
        filmStorage.addLike(filmId, userId); // делегируем в хранилище
    }

    public void removeLike(long filmId, long userId) {
        checkIfUserExists(userId);
        findById(filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> findAllPopular(long count) {
        return filmStorage.findAllPopular(count);
    }

    private void checkIfUserExists(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d не найден".formatted(userId)));
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
