package com.practice.filmorate.service;

import com.practice.filmorate.exception.NotFoundException;
import com.practice.filmorate.exception.ValidationException;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d не найден".formatted(userId)));
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        User existingUser = findById(user.getId());
        existingUser.setName(user.getName());
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = findById(userId); // проверка, существует ли пользователь и получение
        User friend = findById(friendId); // проверка, существует ли пользователь и получение
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> findAllFriends(long userId) {
        User user = findById(userId);

        return user.getFriends().stream()
                .map(this::findById) // из каждого id переводим в объект User
                .toList();
    }

    public List<User> findAllCommonFriends(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        // собираем все общие идентификтаоры во множество
        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(friend.getFriends());

        return commonFriendIds.stream()
                .map(this::findById) // из каждого id переводим в объект User
                .toList();
    }

    private void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы;");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
