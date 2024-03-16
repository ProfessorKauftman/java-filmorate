package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    void createFilmGenre(Film film);

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    void updateFilmByGenre(Film film);

    boolean isGenreExisted(int id);

    void loadGenres(List<Film> films);

}
