package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {
    public static boolean validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("The title of the film is empty");
            throw new ValidationException("The title of the film cannot be empty!");
        }
        if (film.getDescription().length() > 200) {
            log.error("The maximum value of characters has been exceeded!");
            throw new ValidationException("The description should be no more than 200 characters long!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("The release date of the film is earlier than acceptable");
            throw new ValidationException("The release of the film cannot be earlier than 12/25/1895");
        }
        if (film.getDuration().isNegative()) {
            log.error("The duration of the film is less than or equal to 0");
            throw new ValidationException("The duration of the film cannot be less than or equal to 0");
        }
        return true;
    }

    public static boolean validateUser(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.error("The user's Email is empty or does not contain @");
            throw new ValidationException("The Email should not be empty and should contain @");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("The username is empty or contains spaces");
            throw new ValidationException("The username must not be empty or contain spaces!");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Date of birth in the future");
            throw new ValidationException("The date of birth cannot be in the future!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("The Username is empty, the Login will be used");
        }
        return true;
    }

}
