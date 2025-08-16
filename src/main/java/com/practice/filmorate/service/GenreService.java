package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.model.Genre;
import com.practice.filmorate.storage.dbImpl.GenreDbStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(long genreId) {
        return genreStorage.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Mpa с id = %d не найден".formatted(genreId)));
    }
}
