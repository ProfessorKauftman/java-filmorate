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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorsStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        String query = "select * from directors";
        return jdbcTemplate.query(query, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        String query = "select * from directors where director_id=?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(query, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Director with id " + id + " not found");
        }
        return director;
    }

    @Override
    public Director createDirector(Director director) {
        String query = "insert into directors (director_name) values (?)";
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;}, key);
        director.setId((int) key.getKey());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String query = "update directors set director_name = ? where director_id = ? " +
                "and exists (select director_id from directors where director_id = ?)";
        int numUpdatedRows = jdbcTemplate.update(query, director.getName(),
                director.getId(), director.getId());
        if (numUpdatedRows == 0)
            throw new NotFoundException("Director with id " + director.getId() + " not found!");
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String query = "delete from directors where director_id = ?";
        int numUpdatedRows = jdbcTemplate.update(query, id);
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
        String query = "delete from film_director where film_id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public void updateDirectorForFilm(Film film) {
        removeDirectorFilmLinkById(film.getId());
        saveFilmDirectorLink(film);
    }

    @Override
    public List<Director> getDirectorsForFilms(int film_id) {
        String query = "select director_id, director_name from directors " +
                "where director_id in (select director_id from film_director where film_id = ?)";
        return jdbcTemplate.query(query, this::mapRowToDirector, film_id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getInt("director_id"),
                resultSet.getString("director_name"));
    }
}
