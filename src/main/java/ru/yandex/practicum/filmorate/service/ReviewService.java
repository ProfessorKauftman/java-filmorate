package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.OperationTypes;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedService feedService;

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public Review addReview(Review review) {
        filmService.getFilmById(Math.toIntExact(review.getFilmId()));
        userService.getUserById(Math.toIntExact(review.getUserId()));

        Review createdReview = reviewStorage.addReview(review);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(createdReview.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.ADD)
                .entityId(createdReview.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
        return createdReview;
    }

    public Review updateReview(Review review) {
        getReviewById(review.getReviewId());
        Review updatedReview = reviewStorage.updateReview(review);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(updatedReview.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.UPDATE)
                .entityId(updatedReview.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
        return updatedReview;

    }

    public void deleteReviewById(long id) {
        Review review = getReviewById(id);
        reviewStorage.deleteReviewById(id);

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.REMOVE)
                .entityId(review.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
    }

    public void addReviewLike(long reviewId, long userId) {
        getReviewById(reviewId);
        userService.getUserById((int) userId);
        reviewStorage.addReviewLike(reviewId, userId);
    }

    public void deleteReviewLike(long reviewId, long userId) {
        getReviewById(reviewId);
        userService.getUserById((int) userId);
        reviewStorage.deleteReviewLike(reviewId, userId);
    }

    public void addReviewDislike(long reviewId, long userId) {
        getReviewById(reviewId);
        userService.getUserById((int) userId);
        reviewStorage.addReviewDislike(reviewId, userId);
    }

    public void deleteReviewDislike(long reviewId, long userId) {
        getReviewById(reviewId);
        userService.getUserById((int) userId);
        reviewStorage.deleteReviewDislike(reviewId, userId);
    }

    public List<Review> getAllReviews(Long filmId, int count) {
        return reviewStorage.getAll(filmId, count)
                .stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByFilmId(long filmId, int limit) {
        return reviewStorage.getReviewsByFilmId(filmId)
                .stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
