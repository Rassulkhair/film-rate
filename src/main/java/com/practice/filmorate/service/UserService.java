package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.dbImpl.UserDbStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d не найден".formatted(userId)));
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + user.getId() + " не найден"));
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        userStorage.addFriend(userId, friendId);

    }

    public void removeFriend(long userId, long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> findAllFriends(long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Set<Long> friendsIds = userStorage.getAllFriends(userId);

        return friendsIds.stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    public List<User> findAllCommonFriends(Long userId, Long otherUserId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherUserId);

        Set<Long> userFriends = userStorage.getAllFriends(userId);
        Set<Long> otherFriends = userStorage.getAllFriends(otherUserId);

        userFriends.retainAll(otherFriends);

        return userFriends.stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    private User getUserOrThrow(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
