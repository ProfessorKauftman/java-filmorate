package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidatorTest {
    @Test
    public void createFilmWithoutName() {
        Film film = new Film(null, "Описание",
                LocalDate.of(2000, 9, 12), Duration.ofMinutes(180));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Название фильма не может быть пустым!", e.getMessage());
    }

    @Test
    public void createFilmWithBigDescription() {
        Film film = new Film("Маска", "Описание".repeat(30),
                LocalDate.of(1990, 6, 8), Duration.ofMinutes(180));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Описание должно быть не больше 200 символов!", e.getMessage());
    }

    @Test
    public void createFilmWithEarlyDate() {
        Film film = new Film("Спартак", "Описание",
                LocalDate.of(1700, 12, 12), Duration.ofMinutes(180));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Релиз фильма не может быть ранее 25.12.1895", e.getMessage());
    }

    @Test
    public void createFilmWhenDurationZeroOrLess() {
        Film film = new Film("Гарри Поттер", "Описание",
                LocalDate.of(2012, 7, 20), Duration.ofMinutes(-20));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Продолжительность фильма не может быть меньше или равна 0", e.getMessage());
    }

    @Test
    public void createFilmWhenEverythingIsGood() {
        Film film = new Film("Вечное сияние чистого разума", "Описание",
                LocalDate.of(2012, 7, 20), Duration.ofMinutes(170));
        assertDoesNotThrow(() -> Validator.validateFilm(film));
    }

    @Test
    public void createUserWithInvalidEmail() {
        User user = new User("Yandex%yandex.ru", "Tabula",
                LocalDate.of(1994, 10, 17));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Email не должен быть пустым и доолжен содержать @", e.getMessage());
    }

    @Test
    public void createUserWithInvalidLogin() {
        User user = new User("Yandex@yandex.ru", "Tabu la",
                LocalDate.of(1994, 10, 17));
        ValidationException e = assertThrows(ValidationException.class, () -> ru.yandex.practicum.filmorate.validator.Validator.validateUser(user));
        assertEquals("Имя пользователя не должно быть пустым или соджержать пробелы!", e.getMessage());
    }

    @Test
    public void createUserWithEmptyName() {
        User user = new User("Yandex@yandex.ru", "Tabula",
                LocalDate.of(1994, 10, 17));
        ru.yandex.practicum.filmorate.validator.Validator.validateUser(user);
        assertEquals("Tabula", user.getName());
    }

    @Test
    public void createUserWithInvalidBirthday() {
        User user = new User("Yandex@yandex.ru", "Tabula",
                LocalDate.of(2222, 10, 17));
        ValidationException e = assertThrows(ValidationException.class, () -> ru.yandex.practicum.filmorate.validator.Validator.validateUser(user));
        assertEquals("Дата рождения не может быть в будущем!", e.getMessage());
    }

    @Test
    public void createUserWhenEverythingIsGood() {
        User user = new User("Yandex@yandex.ru", "Tabula",
                LocalDate.of(1994, 10, 17));
        assertDoesNotThrow(() -> Validator.validateUser(user));
    }
}