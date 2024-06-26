package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.HandlerRecommendationFilms;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final FeedService feedService;
    private final HandlerRecommendationFilms recommendationsFilms;

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

    public void removeUser(int id) {
        log.info("Delete user with id: {}", id);
        userStorage.removeUser(id);
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

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId((long) id)
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.ADD)
                .entityId((long) friendId)
                .eventId(0L)
                .build();
        feedService.addEvent(event);
    }

    public void removeFriend(int id, int friendId) {
        friendStorage.deleteFriend(id, friendId);
        log.info("Friend with id: {} {} {}", friendId,
                " has been deleted from the friend list of the user with id: ",
                id);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId((long) id)
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.REMOVE)
                .entityId((long) friendId)
                .eventId(0L)
                .build();
        feedService.addEvent(event);
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

    public List<Film> recommendationsFilms(int userId) {
        List<Film> films = recommendationsFilms
                .getRecommendations(userId, likeStorage.getMapUserLikeFilms()).stream()
                .map(filmStorage::getFilmById).collect(Collectors.toList());
        genreStorage.loadGenres(films);

        return films;
    }

}
