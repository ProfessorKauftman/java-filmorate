package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    List<Film> getFilmsByDirector(int directorId);

    List<Film> getFilms();

    Film updateFilm(Film film);

    Film getFilmById(Integer id);

    List<Film> getFavoriteFilms(int id);

    void isFilmExisted(int id);

    void deleteFilmById(int id);

    List<Film> getFilmsByDirectorSortedByLikes(int directorId);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
