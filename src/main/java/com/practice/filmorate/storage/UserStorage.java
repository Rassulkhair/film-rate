package com.practice.filmorate.storage;

import com.practice.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(long userId);

    User create(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getAllFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
