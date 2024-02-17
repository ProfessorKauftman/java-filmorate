package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private final Duration duration;
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(User user) throws ResponseStatusException {
        if (likes.contains(user.getId())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "It is impossible to like a movie twice for the same movie.");
        }
        likes.add(user.getId());
    }

    public void deleteLike(User user) throws ResponseStatusException {
        if (!likes.contains(user.getId())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "It is impossible to remove a like from a movie from a user who did not put it on.");
        }
        likes.remove(user.getId());
    }

}
