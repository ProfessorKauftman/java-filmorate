package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorsStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class DirectorsStorageImpl implements DirectorsStorage {

    private static final String SQL_GET_ALL_DIRECTORS = "SELECT * FROM directors";

    private static final String SQL_GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id=?";

    private static final String SQL_CREATE_DIRECTOR = "INSERT INTO directors (director_name) VALUES (?)";

    private static final String SQL_UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ? " +
            "AND exists (SELECT director_id FROM directors WHERE director_id = ?)";

    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";

    public static final String SQL_REMOVE_DIRECTOR = "DELETE FROM film_director WHERE film_id = ?";

    public static final String SQL_GET_DIRECTORS_FOR_FILMS = "SELECT director_id, director_name FROM directors " +
            "WHERE director_id IN (SELECT director_id FROM film_director WHERE film_id = ?)";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorsStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SQL_GET_ALL_DIRECTORS, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        Director director;
        try {
            director = jdbcTemplate.queryForObject(SQL_GET_DIRECTOR_BY_ID, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Director with id " + id + " not found");
        }
        return director;
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_CREATE_DIRECTOR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, key);
        director.setId((int) key.getKey());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        int numUpdatedRows = jdbcTemplate.update(SQL_UPDATE_DIRECTOR, director.getName(),
                director.getId(), director.getId());
        if (numUpdatedRows == 0)
            throw new NotFoundException("Director with id " + director.getId() + " not found!");
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        int numUpdatedRows = jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
        if (numUpdatedRows == 0)
            throw new NotFoundException("Director with id " + id + " not found!");
    }

    @Override
    public void saveFilmDirectorLink(Film film) {
        if (film == null || film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        List<Director> directors = film.getDirectors();
        int filmId = film.getId();
        String updateQuery = "insert into film_director (director_id, film_id) values (?, ?)";

        jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, directors.get(i).getId());
                ps.setInt(2, filmId);
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

    }

    @Override
    public void removeDirectorFilmLinkById(int id) {
        jdbcTemplate.update(SQL_REMOVE_DIRECTOR, id);
    }

    @Override
    public void updateDirectorForFilm(Film film) {
        removeDirectorFilmLinkById(film.getId());
        saveFilmDirectorLink(film);
    }

    @Override
    public List<Director> getDirectorsForFilms(int filmId) {
        return jdbcTemplate.query(SQL_GET_DIRECTORS_FOR_FILMS, this::mapRowToDirector, filmId);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getInt("director_id"),
                resultSet.getString("director_name"));
    }
}
