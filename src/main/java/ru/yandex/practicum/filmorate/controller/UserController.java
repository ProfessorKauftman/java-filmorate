package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<User> allUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        Validator.validateUser(user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь {} был создан!", user.getEmail());
        return user;
    }

    @PutMapping
    public User addOrUpdateUser(@Valid @RequestBody User user) {
        Validator.validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким ID не найден!");
        }
        users.put(user.getId(), user);
        log.info("Данные о пользователе {} обновлены", user.getEmail());
        return user;
    }

}
