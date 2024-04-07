package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@RequiredArgsConstructor
@Builder
public class Event {
    private Long timestamp;
    private Long userId;
    private EventTypes eventType;
    private OperationTypes operation;
    private Long eventId;
    private Long entityId;


    public Event(Long timestamp, Long userId, EventTypes eventType, OperationTypes operation, Long eventId, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.eventId = eventId;
        this.entityId = entityId;
    }
}
