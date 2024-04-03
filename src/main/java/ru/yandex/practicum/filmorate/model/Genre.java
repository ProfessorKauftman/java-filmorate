package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Genre {
    @NotNull
    private Integer id;
    private String name;

    @NotNull
    @Valid
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
