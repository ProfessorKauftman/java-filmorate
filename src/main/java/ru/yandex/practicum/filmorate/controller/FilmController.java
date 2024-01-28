package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;


import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;


    @GetMapping
    public List<Film> allFilms() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        Validator.validateFilm(film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм {} был добавлен!", film.getName());
        return film;
    }

    @PutMapping
    public Film createOrUpdateFilm(@RequestBody Film film) {
        Validator.validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким ID не найден!");
        }
        films.put(film.getId(), film);
        log.info("Фильм {} обновлен", film.getName());
        return film;
    }

}
