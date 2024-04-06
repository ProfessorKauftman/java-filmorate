package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private static final String SQL_GET_USERS = "SELECT * FROM users;";
    private static final String SQL_INSERT_USER = "INSERT INTO users(name, email, login, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = " UPDATE users SET name = ?, email = ?, login = ?, birthday = ? " +
            "WHERE user_id = ?;";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";

    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

    private static final String SQL_SELECT_USERS_ID = "SELECT user_id FROM users WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_USER, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, (java.sql.Date.valueOf(user.getBirthday())));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        log.info("Update user with id: {}", user.getId());
        return user;
    }

    @Override
    public User getUserById(int id) {
        log.info("GEt user with id: {}", id);
        return jdbcTemplate.queryForObject(SQL_SELECT_USER_BY_ID, this::makeUser, id);
    }

    @Override
    public List<User> allUsers() {
        List<User> userList = jdbcTemplate.query(SQL_GET_USERS, this::makeUser);
        log.info("Number of users: {}", userList.size());
        return userList;
    }

    @Override
    public void removeUser(int id) {
        log.info("Delete user with id: {}", id);
        jdbcTemplate.update(SQL_DELETE_USER, id);
    }

    @Override
    public void isUserExisted(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_SELECT_USERS_ID, id);
        if (!rowSet.next()) {
            throw new NotFoundException("User with id " + id + " doesn't exist");
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("Make user");
        return new User(resultSet.getInt("user_id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getDate("birthday").toLocalDate());
    }
}
