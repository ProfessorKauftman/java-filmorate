package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private static final Comparator<Film> SORTING_FILMS = Comparator
            .comparingInt((Film film) -> film.getLikes().size())
            .reversed();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        List<Film> allFilms = new ArrayList<>(filmStorage.allFilms());
        log.info("Current number of films: {}", filmStorage.allFilms().size());
        return allFilms;
    }

    public Film create(Film film) {
        if (filmStorage.allFilms().contains(film)) {
            log.warn("Film exists");
            throw new IllegalArgumentException("This film exists!");
        }
        if (Validator.validateFilm(film)) {
            filmStorage.createFilm(film);
            log.info("Film {} saved", film);
            return film;
        }
        throw new ValidationException("Validation of the film " + film + " failed");
    }

    public Film update(Film film) {
        if (filmStorage.allFilms()
                .stream()
                .noneMatch(f -> f.getId().equals(film.getId()))) {
            throw new NotFoundException("There is no film with id=" + film.getId());
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
            throw new IllegalArgumentException("id can't be negative or equal to 0. Your id: " + id);
        }
        return filmStorage.allFilms()
                .stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("There is no film with id=" + id));
    }

    public String addLike(Integer userId, Integer filmId) throws ResponseStatusException {
        User user;
        Film film;
        if (userId <= 0 || filmId <= 0) {
            throw new NotFoundException(
                    "userId and filmId cannot be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "Error requesting to delete a like from a movie. " +
                            "It is impossible to delete a like from a user with an id= "
                            + userId + " which does not exist.");
        }
        user = getUserById(userId);
        film = filmStorage.allFilms().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Film not found"));
        film.addLike(user);
        return "User " + user.getName() + " liked the film " + film.getName();
    }

    public String deleteLike(Integer userId, Integer filmId) throws ResponseStatusException {
        User user;
        Film film;
        if (userId <= 0 || filmId <= 0) {
            throw new NotFoundException(
                    "userId and filmId cannot be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "Error requesting to delete a like from a movie. " +
                            "It is impossible to delete a like from a user with an id= "
                            + userId + " which does not exist.");
        }
        user = getUserById(userId);
        film = filmStorage.allFilms().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));
        film.deleteLike(user);
        return "User " + user.getName() + " deleted like from the film: " + film.getName();
    }

    public List<Film> getSortedFilms(Integer count) throws ResponseStatusException {
        if (count <= 0) {
            throw new NotFoundException("count can't be negative or equal to 0");
        }
        return filmStorage.allFilms()
                .stream()
                .sorted(SORTING_FILMS)
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private User getUserById(Integer userId) {
        return userStorage.allUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

}
