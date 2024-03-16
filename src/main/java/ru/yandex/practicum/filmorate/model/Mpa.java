package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Mpa {
    @NotNull
    private Integer id;
    private String name;

    @NotNull
    @Valid
    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
