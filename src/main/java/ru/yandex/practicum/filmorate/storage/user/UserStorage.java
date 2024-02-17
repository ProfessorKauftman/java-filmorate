package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

public interface UserStorage {

    HashMap<Integer, User> users = new HashMap<>();

    List<User> allUsers();
    User createUser(User user);

    void updateUser(User user);
}
