package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    public void testCreateUser(){
        User user = new User();
        user.setName("User");
        user.setEmail("User@yandex.ru");
        user.setLogin("User_Login");
        user.setBirthday(LocalDate.of(1994, 5,4));
        userDbStorage.createUser(user);

        assertNotNull(user.getId());
    }

    @Test
    public void testUpdateUser(){
        User user = new User();
        user.setName("User");
        user.setEmail("User@yandex.ru");
        user.setLogin("User_Login");
        user.setBirthday(LocalDate.of(1994, 5,4));
        userDbStorage.createUser(user);
        user.setName("Update_User");
        userDbStorage.updateUser(user);

        assertEquals("Update_User", user.getName());
    }

    @Test
    public void testGetUserById(){
        User user = new User();
        user.setName("User");
        user.setEmail("User@yandex.ru");
        user.setLogin("User_Login");
        user.setBirthday(LocalDate.of(1994, 5,4));
        userDbStorage.createUser(user);

        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("User1_Login");
        user1.setBirthday(LocalDate.of(1990, 7,17));
        userDbStorage.createUser(user1);

        assertEquals(user.getId(), 1);
        assertEquals(user1.getId(), 2);
    }

    @Test
    public void testIsUserExist(){
        User user = new User();
        user.setName("User");
        user.setEmail("User@yandex.ru");
        user.setLogin("User_Login");
        user.setBirthday(LocalDate.of(1994, 5,4));
        userDbStorage.createUser(user);

        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("User1_Login");
        user1.setBirthday(LocalDate.of(1990, 7,17));
        userDbStorage.createUser(user1);

        assertThrows(NotFoundException.class,() -> userDbStorage.isUserExisted(-1));

    }
}