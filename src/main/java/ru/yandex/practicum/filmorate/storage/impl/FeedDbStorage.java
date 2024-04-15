package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.OperationTypes;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_USER_BY_ID = "SELECT f.event_id, f.created_at, f.user_id, f.event_type," +
            " f.operation, f.entity_id FROM feed AS f WHERE f.user_id = ?;";

    private static final String SQL_ADD_EVENT = "INSERT INTO feed (created_at, user_id, event_type, operation," +
            " entity_id) VALUES (?, ?, ?, ?, ?);";

    @Override
    public List<Event> getByUserId(Long userId) {
        return jdbcTemplate.query(SQL_GET_USER_BY_ID, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public Event addEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_ADD_EVENT, new String[]{"event_id"});
            statement.setLong(1, event.getTimestamp());
            statement.setLong(2, event.getUserId());
            statement.setString(3, event.getEventType().toString());
            statement.setString(4, event.getOperation().toString());
            statement.setLong(5, event.getEntityId());
            return statement;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Long timestamp = rs.getLong("created_at");
        Long userId = rs.getLong("user_id");
        EventTypes eventType = EventTypes.valueOf(rs.getString("event_type"));
        OperationTypes operation = OperationTypes.valueOf(rs.getString("operation"));
        Long eventId = rs.getLong("event_id");
        Long entityId = rs.getLong("entity_id");

        return new Event(timestamp, userId, eventType, operation, eventId, entityId);
    }

}