package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {
    public static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название филма пустое");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.error("Превышено максимальное значение знаков!");
            throw new ValidationException("Описание должно быть не больше 200 символов!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза фильма раньше допустимого");
            throw new ValidationException("Релиз фильма не может быть ранее 25.12.1895");
        }
        if (!film.getDuration().isPositive()) {
            log.error("Продолжительность фильма меньше или равна 0");
            throw new ValidationException("Продолжительность фильма не может быть меньше или равна 0");
        }
    }

    public static void validateUser(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.error("Email пользователя пустой или не содержит @");
            throw new ValidationException("Email не должен быть пустым и доолжен содержать @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Имя пользователя пустое или содержит пробелы");
            throw new ValidationException("Имя пользователя не должно быть пустым или соджержать пробелы!");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован Логин");
        }
    }

    // Хотел уточнить информацию по поводу логов, как сделать формат логов более красивым в выводе?
    // По формату:
    //14:06:49: Событие 1
    //14:27:53: Событие 2

}