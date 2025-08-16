package com.practice.filmorate.storage;

import com.practice.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(long genreId);
}
