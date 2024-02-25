package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws ResponseStatusException {
        if (userStorage.allUsers().contains(user)) {
            log.warn("Such a user already exists");
            throw new IllegalArgumentException("Such a user already exists");
        }
        if (Validator.validateUser(user)) {
            userStorage.createUser(user);
            log.info("User {} saved", user);
            return user;
        }
        throw new ValidationException("Validation of the film " + user + " failed");
    }

    public User updateUser(User user) throws ResponseStatusException {
        if (userStorage.allUsers()
                .stream()
                .noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new NotFoundException("There is no user with id: " + user.getId());
        }
        if (Validator.validateUser(user)) {
            userStorage.updateUser(user);
            log.info("User {} updated", user);
            return user;
        }
        throw new ValidationException("Validation of the film " + user + " failed");
    }

    public List<User> getUsers() {
        List<User> allUsers = new ArrayList<>(userStorage.allUsers());
        log.info("Current number of users: " + userStorage.allUsers().size());
        return allUsers;
    }

    public String addFriend(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new NotFoundException("UserId and friendId can't be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "The error of adding friends! " +
                            "It is impossible to add friends to a user with a non-existent id= " + userId);
        } else {
            user = getUserById(userId);
        }
        if (getUserById(friendId) == null) {
            throw new NotFoundException(
                    "The error of adding friends! " +
                            "It is impossible to add friends to a user with a non-existent id= " + friendId);
        } else {
            friend = getUserById(friendId);
        }
        user.addFriend(friend);
        friend.addFriend(user);
        return user.getName() + " added as a friend " + friend.getName();

    }

    public String deleteFriend(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new NotFoundException(
                    "userId and friendId can't be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "The error of deleting from friends! " +
                            "It is not possible to remove a non-existent user with an id from friends= " + userId);
        } else {
            user = getUserById(userId);
        }
        if (getUserById(friendId) == null) {
            throw new NotFoundException(
                    "The error of deleting from friends! " +
                            "It is not possible to remove a non-existent user with an id from friends= " + friendId);
        } else {
            friend = getUserById(friendId);
        }
        user.deleteFriend(friend);
        friend.deleteFriend(user);
        return user.getName() + " deleted from friends " + friend.getName();
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new NotFoundException(
                    "userId and friendId can't be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "Error requesting a list of mutual friends! " +
                            "It is impossible to get a list of friends of a non-existent user with an id= " + userId);
        } else {
            user = getUserById(userId);
        }
        if (getUserById(friendId) == null) {
            throw new NotFoundException(
                    "Error requesting a list of mutual friends! " +
                            "It is impossible to get a list of friends of a non-existent user with an id= " + friendId);
        } else {
            friend = getUserById(friendId);
        }
        return user.getCommonFriends(friend)
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<User> getFriends(Integer friendId) throws ResponseStatusException {
        User friend;
        if (friendId <= 0) {
            throw new NotFoundException(
                    "userId and friendId can't be negative or equal to 0");
        }
        if (getUserById(friendId) == null) {
            throw new NotFoundException(
                    "Friend list request error! " +
                            "It is impossible to get a list of friends of a non-existent user with an id=" + friendId);
        } else {
            friend = getUserById(friendId);
        }
        return friend.getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public User getUser(Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException(
                    "userId and friendId can't be negative or equal to 0");
        }
        if (getUserById(userId) == null) {
            throw new NotFoundException(
                    "The user with id= " + userId + " does not exist");
        }
        return getUserById(userId);
    }

    private User getUserById(Integer userId) {
        return userStorage.allUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
