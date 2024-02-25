package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Integer nextIdUser = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> allUsers() {
        return users.values();
    }

    @Override
    public void createUser(User user) {
        user.setId(nextIdUser++);
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }


}
