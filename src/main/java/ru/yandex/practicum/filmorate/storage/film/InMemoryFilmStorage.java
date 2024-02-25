package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int nextFilmId = 1;
    private HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> allFilms() {
        return films.values();
    }

    @Override
    public void createFilm(Film film) {
        film.setId(nextFilmId++);
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId(), film);
    }

}
