package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {

    Map<Integer, User> allUsers();

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(User user);
}
