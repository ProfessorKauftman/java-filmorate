package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> getAll(Long filmId, int count);

    Review getReviewById(long id);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(long id);

    void addReviewLike(long reviewId, long userId);

    void deleteReviewLike(long reviewId, long userId);

    void addReviewDislike(long reviewId, long userId);

    void deleteReviewDislike(long reviewId, long userId);

    List<Review> getReviewsByFilmId(long filmId);


}
