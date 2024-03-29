package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public User createUser(User user) {
        validUser(user);
        log.info("New user with id: {}", user.getId());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validUser(user);
        userStorage.isUserExisted(user.getId());
        log.info("User with id: {} {}", user.getId(), " has been updated");
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        log.info("Get {} users", userStorage.allUsers().size());
        return userStorage.allUsers();
    }

    public User getUserById(int id) {
        userStorage.isUserExisted(id);
        User user = userStorage.getUserById(id);
        log.info("Get user with id: {}", id);
        return user;
    }

    public void addFriend(int id, int friendId) {
        userStorage.isUserExisted(id);
        userStorage.isUserExisted(friendId);
        friendStorage.addFriend(id, friendId);
        log.info("Friend with id: {} {} {}", friendId, " has been added to the user with id: ", id);
        log.info("Friend with id: {} {} {}", id, " has been added to the user with id: ", friendId);
    }

    public void removeFriend(int id, int friendId) {
        friendStorage.deleteFriend(id, friendId);
        log.info("Friend with id: {} {} {}", friendId,
                " has been deleted from the friend list of the user with id: ",
                id);
    }

    public List<User> getAllFriends(int id) {
        List<User> friends = friendStorage.getAllFriends(id);
        log.info("Get all user's friends with id: {}", id);
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        log.info("Get users' common friends with id: {} and {}", userId, friendId);
        return friendStorage.getCommonFriends(userId, friendId);
    }

    public void validUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
