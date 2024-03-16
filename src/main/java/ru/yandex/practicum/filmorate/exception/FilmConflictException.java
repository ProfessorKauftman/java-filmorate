package ru.yandex.practicum.filmorate.exception;

public class FilmConflictException extends RuntimeException {
    public FilmConflictException(String msg) {
        super(msg);
    }
}
