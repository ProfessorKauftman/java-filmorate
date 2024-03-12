package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private static final String SQL_INSERT_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private static final String SQL_SELECT_GENRE = "SELECT * FROM genres WHERE genre_id = ?;";
    private static final String SQL_SELECT_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id;";
    private static final String SQL_DELETE_GENRE = "DELETE FROM film_genre WHERE film_id = ?;";
    private static final String SQL_SELECT_GENRE_NAME = "SELECT name FROM genres WHERE genre_id = ?;";

    private static final String SQL_SELECT_LOAD_GENRE = "SELECT genres.genre_id AS genre_id, " +
            "genres.name AS genre_name, " +
            "fg.film_id FROM genres " +
            "INNER JOIN film_genre fg ON genres.GENRE_ID = fg.genre_id WHERE fg.film_id IN (";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createFilmGenre(Film film) {
        if (film == null || film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        try {
            jdbcTemplate.batchUpdate(SQL_INSERT_GENRE, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Iterator<Genre> iterator = film.getGenres().iterator();
                    for (int j = 0; j < i; j++) {
                        iterator.next();
                    }
                    Genre genre = iterator.next();
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
            log.info("Created {} film genre", film.getGenres().size());
        } catch (DataAccessException e) {
            log.error("Error creating film genre: {}" + e.getMessage(), e.getMessage(), e);
        }
    }

    @Override
    public Genre getGenreById(int id) {
        this.isGenreExisted(id);
        log.info("Genre by id: {}", id);
        return jdbcTemplate.queryForObject(SQL_SELECT_GENRE, this::createGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = jdbcTemplate.query(SQL_SELECT_ALL_GENRES, this::createGenre);
        log.info("Extract {} genres from DB", genres.size());
        return genres;
    }

    @Override
    public void updateFilmByGenre(Film film) {
        jdbcTemplate.update(SQL_DELETE_GENRE, film.getId());
        this.createFilmGenre(film);
        log.info("Film {} updated by genre", film.getId());

    }

    @Override
    public boolean isGenreExisted(int id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SQL_SELECT_GENRE_NAME, id);
        if (!sqlRowSet.next()) {
            throw new NotFoundException("Genre id: " + id + " doesn't exist");
        }
        return sqlRowSet.next();
    }


    @Override
    public void loadGenres(List<Film> films) {
        final Map<Integer, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        try {
            String sql = SQL_SELECT_LOAD_GENRE + String.join(",", Collections.nCopies(films.size(), "?"))
                    + ")";
            jdbcTemplate.query(sql, (rs) -> {
                int filmId = rs.getInt("film_id");
                Film film = filmMap.get(filmId);
                if (film != null) {
                    film.addGenre(new Genre(rs.getInt("genre_id"),
                            rs.getString("genre_name")));
                }
            }, films.stream().map(Film::getId).toArray());
        } catch (DataAccessException e) {
            log.info("Load genre is failed: {}", e.getMessage());
            throw new DataException("Error from load genre", e);
        }
    }

    private Genre createGenre(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
    }

}
