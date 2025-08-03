package com.practice.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private final Set<Long> likes = new HashSet<>();

    public void addLike(long userId) {
        // можно добавить условие типо поставил ли данный пользователь лайк,
        // но этого не было в тз
        likes.add(userId);
    }

    public void removeLike(long userId) {
        // аналогично здесь как в лайке
        likes.remove(userId);
    }
}
