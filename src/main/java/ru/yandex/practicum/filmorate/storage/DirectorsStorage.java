package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorsStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    void saveFilmDirectorLink(Film film);

    void updateDirectorForFilm(Film film);

    void removeDirectorFilmLinkById(int id);

    List<Director> getDirectorsForFilms(int filmId);
}
