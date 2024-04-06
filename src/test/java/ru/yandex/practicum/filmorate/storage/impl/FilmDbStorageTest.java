package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.FilmConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class FilmDbStorageTest {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));

        Film createFilm = filmStorage.createFilm(film);

        assertNotNull(createFilm.getId());
        assertEquals(film.getName(), createFilm.getName());
        assertEquals(film.getDescription(), createFilm.getDescription());
        assertEquals(film.getReleaseDate(), createFilm.getReleaseDate());
        assertEquals(film.getDuration(), createFilm.getDuration());
        assertEquals(film.getMpa().getId(), createFilm.getMpa().getId());
        assertEquals(film.getMpa().getName(), createFilm.getMpa().getName());
    }

    @Test
    public void testGetFilms() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(new LinkedHashSet<>());

        Film film1 = new Film();
        film1.setName("Test1 film");
        film1.setDescription("Test1 Description");
        film1.setReleaseDate(LocalDate.of(2024, 1, 20));
        film1.setDuration(240);
        film1.setMpa(new Mpa(3, "PG-13"));
        film1.setGenres(new LinkedHashSet<>());

        filmStorage.createFilm(film);
        filmStorage.createFilm(film1);
        List<Film> films = filmStorage.getFilms();

        assertFalse(films.isEmpty());
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        Film creatFilm = filmStorage.createFilm(film);
        creatFilm.setName("New Test film");
        Film updatedFilm = filmStorage.updateFilm(creatFilm);

        assertEquals(creatFilm.getId(), updatedFilm.getId());
        assertEquals("New Test film", updatedFilm.getName());
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        Film creatFilm = filmStorage.createFilm(film);

        Film gettingFilm = filmStorage.getFilmById(creatFilm.getId());

        assertEquals(creatFilm.getId(), gettingFilm.getId());
        assertEquals(creatFilm.getName(), gettingFilm.getName());

    }

    @Test
    public void testIsFilmExisted() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));

        Film creatFilm = filmStorage.createFilm(film);

        assertDoesNotThrow(() -> filmStorage.isFilmExisted(creatFilm.getId()));
        assertThrows(NotFoundException.class, () -> filmStorage.isFilmExisted(-1));
    }

    @Test
    public void testAddLike() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        filmStorage.createFilm(film);

        User user = new User();
        user.setName("User");
        user.setEmail("User@yandex.ru");
        user.setLogin("User login");
        user.setBirthday(LocalDate.of(1994, 2, 10));
        userStorage.createUser(user);

        likeStorage.addLike(film.getId(), user.getId());

        assertEquals(filmStorage.getFavoriteFilms(film.getId()), filmStorage.getFavoriteFilms(1));

    }

    @Test
    public void testDeleteFilmById() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 20));
        film.setDuration(240);
        film.setMpa(new Mpa(1, "G"));
        filmStorage.createFilm(film);

        filmStorage.deleteFilmById(film.getId());

        assertEquals(filmStorage.getFilms().size(), 0);
    }
}