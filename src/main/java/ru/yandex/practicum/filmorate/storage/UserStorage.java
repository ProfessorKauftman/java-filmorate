package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> allUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void isUserExisted(int id);


}
