package ru.yandex.practicum.filmorate.storage.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HandlerRecommendationFilms {

    private Map<Integer, List<Integer>> userLikes = new HashMap<>();

    public List<Integer> getRecommendations(int userId, Map<Integer, List<Integer>> userLikes) {
        this.userLikes = userLikes;
        List<Integer> recommendations = new ArrayList<>();

        if (userLikes.isEmpty()) {
            return recommendations;
        }

        List<Integer> similarTastes = findUsersWithSimilarTastes(userId);

        for (int user : similarTastes) {
            List<Integer> newLikes = findDiffLikes(user, userId);
            recommendations.addAll(newLikes);
        }

        return recommendations;
    }

    // Метод для нахождения пользователей с максимальным количеством пересечения
    private List<Integer> findUsersWithSimilarTastes(int userId) {

        List<Integer> usersWithSimilarTastes = new ArrayList<>();
        int maxIntersection = 0;

        for (int user : userLikes.keySet()) {
            if (user != userId) {
                //получаю фильмы пользователя из userLikes
                List<Integer> currentUserLikes = userLikes.get(user);
                //сохраняю фильмы пользователя переданного в метод
                List<Integer> intersection = new ArrayList<>(userLikes.get(userId));
                // Получаю пересечение по фильмам
                intersection.retainAll(currentUserLikes);

                if (intersection.size() > maxIntersection) {
                    maxIntersection = intersection.size();
                    usersWithSimilarTastes.clear();
                    usersWithSimilarTastes.add(user);

                } else if (intersection.size() == maxIntersection) {
                    usersWithSimilarTastes.add(user);
                }
            }
        }
        // Возвращаю коллекцию с максимальными пересечениями
        return usersWithSimilarTastes;
    }

    // Получение фильмов, которые один пролайкал, а другой нет.
    private List<Integer> findDiffLikes(int userOne, int userTwo) {

        List<Integer> diffLikes = new ArrayList<>(userLikes.get(userOne));
        diffLikes.removeAll(userLikes.get(userTwo));

        return diffLikes;
    }


}
