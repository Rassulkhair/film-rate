package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.model.Mpa;
import com.practice.filmorate.storage.dbImpl.MpaDbStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MpaService {
    private final MpaDbStorage mpaStorage;

    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(long mpaId) {
        return mpaStorage.findById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa с id = %d не найден".formatted(mpaId)));
    }
}
