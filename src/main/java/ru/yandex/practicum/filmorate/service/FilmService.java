package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Comparator;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;
    private final DirectorsStorage directorsStorage;
    private static final int MIN_ID = 0;

    public Film addFilm(Film film) {
        mpaStorage.isMpaExisted(film.getMpa().getId());
        filmStorage.createFilm(film);
        genreStorage.createFilmGenre(film);
        directorsStorage.saveFilmDirectorLink(film);
        log.info("Added film with id: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.isFilmExisted(film.getId());
        genreStorage.updateFilmByGenre(film);
        mpaStorage.isMpaExisted(film.getMpa().getId());
        filmStorage.updateFilm(film);
        directorsStorage.updateDirectorForFilm(film);
        log.info("Update film with id: {}", film.getId());
        return film;
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        genreStorage.loadGenres(films);
        log.info("Get {} films", films.size());
        return films;
    }

    public Film getFilmById(int filmId) {
        filmStorage.isFilmExisted(filmId);
        Film film = filmStorage.getFilmById(filmId);
        genreStorage.loadGenres(List.of(film));
        log.info("Get film by id: {}", filmId);
        return film;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.isFilmExisted(filmId);
        userStorage.isUserExisted(userId);
        likeStorage.addLike(filmId, userId);
        log.info("Like added to teh film with id: {} ", filmId);
    }

    public void removeLike(int filmId, int userId) {
        if (filmId < MIN_ID || userId < MIN_ID) {
            throw new NotFoundException("Id can't be negative");
        }
        likeStorage.deleteLike(filmId, userId);
        log.info("User with id: {} remove like from the film with id: {} ", userId, filmId);
    }

    public void deleteFilmById(int id) {
        filmStorage.isFilmExisted(id);
        filmStorage.deleteFilmById(id);
        directorsStorage.removeDirectorFilmLinkById(id);
        log.info("Film with id: {} was deleted", id);
    }

    public List<Film> getFilmsSortedByYears(int directorId) {
        List<Film> films = getFilmsByDirector(directorId);
        genreStorage.loadGenres(films);
        films.sort(Comparator.comparing(Film::getReleaseDate));
        return films;
    }

    public List<Film> getFilmsSortedByLikes(int directorId) {
        List<Film> films = filmStorage.getFilmsByDirectorSortedByLikes(directorId);
        genreStorage.loadGenres(films);
        return films;
    }

    private List<Film> getFilmsByDirector(int directorId) {
        List<Film> films = filmStorage.getFilmsByDirector(directorId);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> favouriteFilms(Integer number) {
        List<Film> films = filmStorage.getFavoriteFilms(number);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> getFavoriteFilmsByGenreAndYear(int limit, int genreId, String releaseDate) {
        List<Film> films = filmStorage.getFavoriteFilmsByGenreAndYear(genreId, releaseDate, limit);
        genreStorage.loadGenres(films);
        log.info("Get {} films", films.size());
        log.info("limit={}, genreId={}, data={}", limit, genreId, releaseDate);
        return films;
    }

    public List<Film> searchByDirector(String query) {
        List<Film> films = filmStorage.searchByDirector(query);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> searchByTitle(String query) {
        List<Film> films = filmStorage.searchByTitle(query);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> searchByTitleAndDirector(String query) {
        List<Film> films = filmStorage.searchByTitleAndDirector(query);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) throws NotFoundException {
        return filmStorage.getCommonFilms(userId, friendId);

    }
}
