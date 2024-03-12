package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getCommonFriends(int id, int friendId);

    List<User> getAllFriends(int id);


}
