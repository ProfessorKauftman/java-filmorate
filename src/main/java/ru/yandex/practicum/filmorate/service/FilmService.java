package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    private int id = 1;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        log.info("Current number of films: {}", filmStorage.allFilms().size());
        return new ArrayList<>(filmStorage.allFilms().values());
    }

    public Film create(Film film) {
        if (filmStorage.allFilms().containsValue(film)) {
            log.warn("Film exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This file exists!");
        }
        if (Validator.validateFilm(film)) {
            film.setId(getNextId());
            filmStorage.createFilm(film);
            log.info("Film {} saved", film);
            return film;
        }
        throw new ValidationException("Validation of the film " + film + " failed");
    }

    public Film update(Film film) {
        if (filmStorage.allFilms().get(film.getId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no film with id=" + film.getId());
        }
        if (Validator.validateFilm(film)) {
            filmStorage.updateFilm(film);
            log.info("Film {} updated", film);
            return film;
        }
        throw new ValidationException("Validation of the film " + film + " failed");
    }

    public Film getFilm(Integer id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id can't be negative or equal to 0");
        }
        if (filmStorage.allFilms().get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no film with id=" + id);
        }
        return filmStorage.allFilms().get(id);
    }

    public String addLike(Integer userId, Integer filmId) throws ResponseStatusException {
        User user;
        Film film;
        if (userId <= 0 || filmId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and filmId cannot be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting to add a like to a movie. It is impossible to put a like from a user with an id= "
                            + userId + " which does not exist.");
        } else {
            user = userStorage.allUsers().get(userId);
        }
        if (filmStorage.allFilms().get(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting to add a like to a movie. It is impossible to put a like a film with an id= "
                            + filmId + " which does not exist.");
        } else {
            film = filmStorage.allFilms().get(filmId);
        }
        film.addLike(user);
        return "User " + user.getName() + " liked the film" + film.getName();
    }

    public String deleteLike(Integer userId, Integer filmId) throws ResponseStatusException {
        User user;
        Film film;
        if (userId <= 0 || filmId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and filmId cannot be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting to delete a like from a movie. " +
                            "It is impossible to delete a like from a user with an id= "
                            + userId + " which does not exist.");
        } else {
            user = userStorage.allUsers().get(userId);
        }
        if (filmStorage.allFilms().get(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting to add a like to a movie. " +
                            "It is impossible to delete a like from a film with an id= "
                            + filmId + " which does not exist.");
        } else {
            film = filmStorage.allFilms().get(filmId);
        }
        film.deleteLike(user);
        return "User " + user.getName() + " deleted like from the film: " + film.getName();
    }

    public List<Film> getSortedFilms(Integer count) throws ResponseStatusException {
        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "count can't be negative or equal to 0");
        }
        Comparator<Film> sortingFilms = (f1, f2) -> {
            Integer filmLikes1 = f1.getLikes().size();
            Integer filmLikes2 = f2.getLikes().size();
            return -1 * filmLikes1.compareTo(filmLikes2);
        };
        return filmStorage.allFilms()
                .values()
                .stream()
                .sorted(sortingFilms)
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Integer getNextId() {
        return id++;
    }


}
