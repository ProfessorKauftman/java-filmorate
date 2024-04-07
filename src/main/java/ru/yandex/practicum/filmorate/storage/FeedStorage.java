package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> getByUserId(Long userId);

    Event addEvent(Event event);
}
