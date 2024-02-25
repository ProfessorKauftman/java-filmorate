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
        assertEquals("The title of the film cannot be empty!", e.getMessage());
    }

    @Test
    public void createFilmWithBigDescription() {
        Film film = new Film("Маска", "Описание".repeat(30),
                LocalDate.of(1990, 6, 8), Duration.ofMinutes(180));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("The description should be no more than 200 characters long!", e.getMessage());
    }

    @Test
    public void createFilmWithEarlyDate() {
        Film film = new Film("Спартак", "Описание",
                LocalDate.of(1700, 12, 12), Duration.ofMinutes(180));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("The release of the film cannot be earlier than 12/25/1895", e.getMessage());
    }

    @Test
    public void createFilmWhenDurationZeroOrLess() {
        Film film = new Film("Гарри Поттер", "Описание",
                LocalDate.of(2012, 7, 20), Duration.ofMinutes(-20));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("The duration of the film cannot be less than or equal to 0", e.getMessage());
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
        assertEquals("The Email should not be empty and should contain @", e.getMessage());
    }

    @Test
    public void createUserWithInvalidLogin() {
        User user = new User("Yandex@yandex.ru", "Tabu la",
                LocalDate.of(1994, 10, 17));
        ValidationException e = assertThrows(ValidationException.class, () -> ru.yandex.practicum.filmorate.validator.Validator.validateUser(user));
        assertEquals("The username must not be empty or contain spaces!", e.getMessage());
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
        assertEquals("The date of birth cannot be in the future!", e.getMessage());
    }

    @Test
    public void createUserWhenEverythingIsGood() {
        User user = new User("Yandex@yandex.ru", "Tabula",
                LocalDate.of(1994, 10, 17));
        assertDoesNotThrow(() -> Validator.validateUser(user));
    }
}
