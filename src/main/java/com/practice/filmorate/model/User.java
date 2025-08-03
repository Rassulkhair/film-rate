package com.practice.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String name;
    private String login;
    private String email;
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long friendId) {
        this.friends.add(friendId);
    }

    public void removeFriend(long friendId) {
        this.friends.remove(friendId);
    }
}
