package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    //@Positive Если я оставляю это аннотацию в Postman мне приходит ошибка 500, это норма?
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private final Duration duration;
}
