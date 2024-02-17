package ru.yandex.practicum.filmorate.storage;

import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{


    private static HashMap<Integer, Film> films = new HashMap<>();
    @Override
    public Map<Integer, Film> allFilms(){
        return films;
    }

    @Override
    public void createFilm(Film film){
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film){
        films.put(film.getId(), film);
    }

    @Override
    public void delete(Film film){
        films.remove(film.getId(), film);
    }

}
