package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public List<Event> getByUserId(Long userId) {
        return feedStorage.getByUserId(userId);
    }

    public Event addEvent(Event event) {
        return feedStorage.addEvent(event);
    }

}
