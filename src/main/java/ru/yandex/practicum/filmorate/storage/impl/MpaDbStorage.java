package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private static final String SQL_SELECT_MPA_ID = "SELECT * FROM mpa_rating WHERE rating_id = ?;";
    private static final String SQL_SELECT_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY rating_id;";
    private static final String SQL_SELECT_MPA_NAME = "SELECT name FROM mpa_rating WHERE rating_id = ?;";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(int id) {
        if (!this.isMpaExisted(id)) {
            throw new NotFoundException("Mpa id: " + id + " doesn't exist");
        }
        log.info("Extract mpa with id: {}", id);
        return jdbcTemplate.queryForObject(SQL_SELECT_MPA_ID, this::createMpa, id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> mpaList = jdbcTemplate.query(SQL_SELECT_ALL_MPA, this::createMpa);
        log.info("Extract {} mpa", mpaList.size());
        return mpaList;
    }

    @Override
    public boolean isMpaExisted(int id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SQL_SELECT_MPA_NAME, id);
        return sqlRowSet.next();
    }

    private Mpa createMpa(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Mpa(resultSet.getInt("rating_id"), resultSet.getString("name"));
    }
}
