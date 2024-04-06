package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorsStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private static final String SQL_CHECK_RATING_EXISTS =
            "SELECT COUNT(*) FROM mpa_rating WHERE rating_id = ?";
    private static final String SQL_CHECK_GENRE_EXISTS =
            "SELECT COUNT(*) FROM FILM_GENRE WHERE GENRE_ID = ?";

    private static final String SQL_GET_FILMS_BY_DIRECTOR_QUERY = "SELECT f.*, mr.name AS mpa_name FROM films f " +
            "LEFT JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id " +
            "WHERE film_id in (SELECT film_id FROM film_director " +
            "WHERE director_id = ?)";

    private static final String SQL_FILMS_BY_DIRECTOR_SORTED_BY_LIKES = "SELECT f.film_id, f.name, f.description, " +
            "f.release_date, f.duration, mr.rating_id as rating_id, mr.name as mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id " +
            "WHERE f.film_id IN (" +
            "    SELECT fd.film_id " +
            "    FROM film_director fd " +
            "    LEFT JOIN likes l ON fd.film_id = l.film_id " +
            "    WHERE fd.director_id = ? " +
            "    GROUP BY fd.film_id " +
            "    ORDER BY COUNT(l.user_id) DESC " +
            ");";

    private static final String SQL_GET_COMMON_FILMS = "SELECT film_id FROM likes WHERE user_id = ? " +
            "INTERSECT SELECT film_id FROM likes WHERE user_id = ? GROUP BY user_id;";

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final DirectorsStorage directorsStorage;

    @Override
    public Film createFilm(Film film) {
        if (!mpaStorage.isMpaExisted(film.getMpa().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "MPA rating with ID " + film.getMpa().getId() + " does not exist.");
        }
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
        isFilmExisted(film.getId());
        if (!mpaStorage.isMpaExisted(film.getMpa().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "MPA rating with ID " + film.getMpa().getId() + " does not exist.");
        }
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
    public List<Film> getFilmsByDirector(int directorId) {
        return jdbcTemplate.query(SQL_GET_FILMS_BY_DIRECTOR_QUERY, this::makeFilm, directorId);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Film with id: " + id + " doesn't exist");
        }
        log.info("Film with id: {} exists in DB", id);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("rating_id"), resultSet.getString("mpa_name"));
        Film film = new Film(resultSet.getInt("film_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"), mpa, new LinkedHashSet<>());
        List<Director> directors = directorsStorage.getDirectorsForFilms(film.getId());
        film.setDirectors(directors);
        return film;
    }

    @Override
    public void deleteFilmById(int id) {
        isFilmExisted(id);
        jdbcTemplate.update(SQL_DELETE_FILM_BY_ID, id);
        log.info("Film with id: {} was deleted from DB", id);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(int directorId) {
        List<Film> films = jdbcTemplate.query(SQL_FILMS_BY_DIRECTOR_SORTED_BY_LIKES, this::makeFilm, directorId);
        if (films.isEmpty()) {
            throw new NotFoundException("Director with id " + directorId + " not found!");
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_COMMON_FILMS, userId, friendId);
        List<Film> commonFilms = new ArrayList<>();
        while (rowSet.next()) {
            commonFilms.add(getFilmById(rowSet.getInt("film_id")));
        }
        return commonFilms;
    }
}


