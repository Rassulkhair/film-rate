package com.practice.filmorate.storage.impl;

import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 0;

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId)); // возвращаем Optional в котором может отсутстовать объект
    }

    @Override
    public User create(User user) {
        user.setId(++nextId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
