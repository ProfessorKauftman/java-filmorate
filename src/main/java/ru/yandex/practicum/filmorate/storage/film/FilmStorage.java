package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> allFilms();

    void createFilm(Film film);

    void updateFilm(Film film);

    void delete(Film film);

}
