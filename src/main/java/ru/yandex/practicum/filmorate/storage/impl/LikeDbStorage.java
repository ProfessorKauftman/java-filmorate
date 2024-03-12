package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Slf4j
@Component
@Qualifier
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private static final String SQL_INSERT_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
    private static final String SQL_DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int id, int userId) {
        jdbcTemplate.update(SQL_INSERT_LIKE, id, userId);
        log.info("Added like for film with id {} from user with id: {}", id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, id, userId);
        log.info("Remove like from film with id: {} by user with id: {}", id, userId);
    }
}
