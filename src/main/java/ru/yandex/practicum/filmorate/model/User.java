package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class User {
    private Integer id;
    @Email
    private final String email;
    @NotBlank
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(User user) throws ResponseStatusException {
        if (friends.contains(user.getId())) {
            throw new RuntimeException(
                    "It is not possible to add a friend to a user who is already a friend");
        }
        friends.add(user.getId());
    }

    public void deleteFriend(User user) throws ResponseStatusException {
        if (!friends.contains(user.getId())) {
            throw new RuntimeException(
                    "It is not possible to delete a user who is not a friend");
        }
        friends.remove(user.getId());
    }

    public List<Integer> getCommonFriends(User friend) {
        return friend.getFriends()
                .stream()
                .filter(friends::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<Integer> getFriends() {
        return new HashSet<>(friends);
    }
}
