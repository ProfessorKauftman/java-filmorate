package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {
    private final FilmService filmService;
    private final GenreService genreService;

    @Test
    public void testCreatedFilmGenre() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(new LinkedHashSet<>());
        film.getGenres().add(new Genre(1, "Комедия"));
        filmService.addFilm(film);

        assertEquals(1, filmService.getFilmById(1).getGenres().size());
    }

    @Test
    public void testGetGenreById() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(new LinkedHashSet<>());
        film.getGenres().add(new Genre(1, "Комедия"));
        filmService.addFilm(film);

        assertEquals(genreService.getGenreById(1).getName(), "Комедия");
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        assertEquals(6, genres.size());
    }

    @Test
    public void testUpdatedFilmGenre() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(new LinkedHashSet<>());
        film.getGenres().add(new Genre(1, "Комедия"));
        filmService.addFilm(film);
        film.getGenres().add(new Genre(3, "Мультфильм"));
        filmService.updateFilm(film);

        assertEquals(filmService.getFilmById(1).getGenres().size(), 2);
    }

}