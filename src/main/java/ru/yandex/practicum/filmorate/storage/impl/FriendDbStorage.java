package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?);";

    private static final String SQL_DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;";

    public static final String SQL_SELECT_COMMON_FRIENDS = "SELECT * FROM users WHERE user_id IN" +
            " (SELECT friend_id FROM friendship WHERE user_id = ?)" +
            " AND user_id IN (SELECT friend_id FROM friendship WHERE user_id = ?);";

    public static final String SQL_SELECT_ALL_FRIENDS = "SELECT * FROM users WHERE user_id IN" +
            " (SELECT friend_id AS id FROM friendship WHERE user_id = ?);";

    private static final String SQL_CHECK_USER_EXISTS = "SELECT COUNT(*) FROM users WHERE user_id = ?;";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int id, int friendId) {
        log.info("Add friend with id: {} to user with id: {}", id, friendId);
        jdbcTemplate.update(SQL_INSERT_FRIEND, id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (!userExists(id) || !userExists(friendId)) {
            throw new NotFoundException("User with ID: " + id + " does not exist");
        }
        log.info("Delete friend with id: {} from user with id: {}", friendId, id);
        jdbcTemplate.update(SQL_DELETE_FRIEND, id, friendId);
    }

    @Override
    public List<User> getCommonFriends(int id, int friendId) {
        if (!userExists(id)) {
            throw new NotFoundException("User with ID: " + id + " does not exist");
        }
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_SELECT_COMMON_FRIENDS, id, friendId);
        List<User> commonFriends = new ArrayList<>();
        while (rowSet.next()) {
            commonFriends.add(new User(rowSet.getInt("user_id"),
                    rowSet.getString("name"),
                    rowSet.getString("email"),
                    rowSet.getString("login"),
                    Objects.requireNonNull(rowSet.getDate("birthday")).toLocalDate()));
        }
        log.info("Common friends: {}", commonFriends.size());
        return commonFriends.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<User> getAllFriends(int id) {
        if (!userExists(id)) {
            throw new NotFoundException("User with ID: " + id + " does not exist");
        }
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_SELECT_ALL_FRIENDS, id);
        List<User> allFriends = new ArrayList<>();
        while (rowSet.next()) {
            allFriends.add(new User(rowSet.getInt("user_id"),
                    rowSet.getString("name"),
                    rowSet.getString("email"),
                    rowSet.getString("login"),
                    Objects.requireNonNull(rowSet.getDate("birthday")).toLocalDate()));
        }
        log.info("Get all friends of user with id: {}", id);
        return allFriends;
    }
    public boolean userExists(int userId) {
        Integer count = jdbcTemplate.queryForObject(SQL_CHECK_USER_EXISTS, new Object[]{userId}, Integer.class);
        return count != null && count > 0;
    }
}
