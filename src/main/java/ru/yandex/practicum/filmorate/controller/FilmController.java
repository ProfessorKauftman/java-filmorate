package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Added film: {} ", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Updated film: {} ", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Get {} films", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Get film by id: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Users with id: {} {} {} ", userId, " added like for the film with id: {}", id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Users with id: {} {} {} ", userId, " has removed like from the film with id: {}", id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") int count) {
        log.debug("Get popular films");
        return filmService.favouriteFilms(count);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        log.info("Film with id: {} was deleted: ", id);
        filmService.deleteFilmById(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId,
                                     @RequestParam(name = "sortBy", defaultValue = "year") String sortBy) {
        if (sortBy.equals("likes")) {
            return filmService.getFilmsSortedByLikes(directorId);
        }

        return filmService.getFilmsSortedByYears(directorId);
    }
}
