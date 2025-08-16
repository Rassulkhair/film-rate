package com.practice.filmorate.storage.dbImpl;

import com.practice.filmorate.model.User;

import com.practice.filmorate.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT = """
            SELECT * FROM users
            """;


    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SELECT, this::mapRow);
    }

    @Override
    public Optional<User> findById(long userId) {
        String sql = SELECT + "WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, userId)
                .stream()
                .findFirst();

    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("login", user.getLogin());
        map.put("name", user.getName());
        map.put("birthday", user.getBirthday());
        long id = jdbcInsert.executeAndReturnKey(map).intValue();


        user.setId(id);

        return user;
    }


    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Long> getAllFriends(Long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        ));
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        return List.of();
    }

    private User mapRow(ResultSet rs, int i) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String login = rs.getString("login");
        String email = rs.getString("email");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, name, login, email, birthday);
    }
}
