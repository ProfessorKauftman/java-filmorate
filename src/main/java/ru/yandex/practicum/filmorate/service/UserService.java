package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    private Integer id = 1;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws ResponseStatusException {
        if (userStorage.allUsers().containsValue(user)) {
            log.warn("Such a user already exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Such a user already exists");
        }
        user.setId(getUserId());
        if (Validator.validateUser(user)) {
            userStorage.createUser(user);
            log.info("User {} saved", user);
            return user;
        }
        throw new ValidationException("Validation of the film " + user + " failed");
    }

    public User updateUser(User user) throws ResponseStatusException {
        if (userStorage.allUsers().get(user.getId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user with id: " + user.getId());
        }
        if (Validator.validateUser(user)) {
            userStorage.updateUser(user);
            log.info("User {} updated", user);
            return user;
        }
        throw new ValidationException("Validation of the film " + user + " failed");
    }

    public List<User> getUsers() {
        log.info("Current number of users: " + userStorage.allUsers().size());
        return new ArrayList<>(userStorage.allUsers().values());
    }

    public String addFriend(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and friendId can't be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The error of adding friends! " +
                            "It is impossible to add friends to a user with a non-existent id= " + userId);
        } else {
            user = userStorage.allUsers().get(userId);
        }
        if (userStorage.allUsers().get(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The error of adding friends! " +
                            "It is impossible to add friends to a user with a non-existent id= " + friendId);
        } else {
            friend = userStorage.allUsers().get(friendId);
        }
        user.addFriend(friend);
        friend.addFriend(user);
        return user.getName() + " added as a friend " + friend.getName();

    }

    public String deleteFriend(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and friendId can't be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The error of deleting from friends! " +
                            "It is not possible to remove a non-existent user with an id from friends= " + userId);
        } else {
            user = userStorage.allUsers().get(userId);
        }
        if (userStorage.allUsers().get(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The error of deleting from friends! " +
                            "It is not possible to remove a non-existent user with an id from friends= " + friendId);
        } else {
            friend = userStorage.allUsers().get(friendId);
        }
        user.deleteFriend(friend);
        friend.deleteFriend(user);
        return user.getName() + " deleted from friends " + friend.getName();
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) throws ResponseStatusException {
        User user;
        User friend;
        if (userId <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and friendId can't be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting a list of mutual friends! " +
                            "It is impossible to get a list of friends of a non-existent user with an id= " + userId);
        } else {
            user = userStorage.allUsers().get(userId);
        }
        if (userStorage.allUsers().get(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error requesting a list of mutual friends! " +
                            "It is impossible to get a list of friends of a non-existent user with an id= " + friendId);
        } else {
            friend = userStorage.allUsers().get(friendId);
        }
        return user.getCommonFriends(friend)
                .stream()
                .map(id -> userStorage.allUsers().get(id))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<User> getFriends(Integer friendId) throws ResponseStatusException {
        User friend;
        if (friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and friendId can't be negative or equal to 0");
        }
        if (userStorage.allUsers().get(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Friend list request error! " +
                            "It is impossible to get a list of friends of a non-existent user with an id=" + friendId);
        } else {
            friend = userStorage.allUsers().get(friendId);
        }
        return friend.getFriends()
                .stream()
                .map(id -> userStorage.allUsers().get(id))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public User getUser(Integer userId) {
        if (userId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "userId and friendId can't be negative or equal to 0");
        }
        if (userStorage.allUsers().get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The user with id= " + userId + " does not exist");
        }
        return userStorage.allUsers().get(userId);
    }

    private Integer getUserId() {
        return id++;
    }

}
