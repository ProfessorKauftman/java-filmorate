package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface LikeStorage {

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    Map<Integer, List<Integer>> getMapUserLikeFilms();
}
