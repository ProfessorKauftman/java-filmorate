package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmConflictException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_GET_FILMS = "SELECT f.*, mr.name AS mpa_name FROM films AS f " +
            "LEFT JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id;";

    private static final String SQL_UPDATE_FILM = "UPDATE films SET " + "name = ?," + "description = ?,"
            + "release_date = ?," + "duration = ?," + "rating_id = ?" + "WHERE film_id = ?";

    private static final String SQL_INSERT_FILM = "INSERT INTO films (name,description,release_date,duration,rating_id)"
            + "VALUES (?,?,?,?,?)";

    private static final String SQL_GET_ID_FILM = "SELECT f.*, mr.name AS mpa_name FROM films AS f " +
            "LEFT JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id WHERE f.film_id = ?";

    private static final String SQL_FAVORITE_FILM = "SELECT f.*, mr.name AS mpa_name FROM films AS f " +
            "LEFT JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id  GROUP BY f.film_id,  mr.name " +
            "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String SQL_DELETE_FILM_BY_ID = "DELETE FROM films" +
            " WHERE film_id = ?";

    private static final String SQL_EXACT_FILM_ID = "SELECT film_id FROM films WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        KeyHolder id = new GeneratedKeyHolder();
        int sqlInsert = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILM, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, id);
        if (sqlInsert != 1) {
            throw new DataAccessException("Fail with insertion film in DB") {
            };
        }
        film.setId(Objects.requireNonNull(id.getKey()).intValue());
        log.info("Add film with id: {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SQL_UPDATE_FILM, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.info("Film was updated with id: {}", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.info("List of {} films from BD", jdbcTemplate.queryForObject("SELECT COUNT (*) FROM FILMS",
                Integer.class));
        return jdbcTemplate.query(SQL_GET_FILMS, this::makeFilm);

    }

    @Override
    public List<Film> getFavoriteFilms(int id) {
        log.info("Film with id: {} from DB", id);
        return jdbcTemplate.query(SQL_FAVORITE_FILM, this::makeFilm, id);
    }

    @Override
    public Film getFilmById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_ID_FILM, this::makeFilm, id);
    }

    @Override
    public void isFilmExisted(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_EXACT_FILM_ID, id);
        if (!rowSet.next()) {
            throw new FilmConflictException("Film with id: " + id + " doesn't exist");
        }
        log.info("Film with id: {} exists in DB", id);
    }


    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("rating_id"), resultSet.getString("mpa_name"));
        return new Film(resultSet.getInt("film_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"), mpa, new LinkedHashSet<>());
    }

    @Override
    public void deleteFilmById(int id) {
        isFilmExisted(id);
        jdbcTemplate.update(SQL_DELETE_FILM_BY_ID, id);
        log.info("Film with id: {} was deleted from DB", id);
    }
}


