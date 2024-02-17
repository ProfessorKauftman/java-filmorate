package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();
    @Override
    public List<User> allUsers(){
        return new ArrayList<>(users.values());
    }
    @Override
    public User createUser(User user){
     user.setId(++userId);
     users.put(user.getId(), user);
     return user;
    }

    @Override
    public void updateUser(User user){
        users.put(user.getId(), user);
    }


}
