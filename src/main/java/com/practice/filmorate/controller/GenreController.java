package com.practice.filmorate.controller;

import com.practice.filmorate.model.Genre;

import com.practice.filmorate.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/{genreId}")
    public Genre findById(@PathVariable long genreId) {
        return genreService.findById(genreId);
    }
}
