package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void testGetMpaById(){
        int id = 3;
        Mpa mpa = mpaDbStorage.getMpaById(id);
        assertEquals(mpa.getName(), "PG-13");
    }

    @Test
    public void testGetAllMpa(){
        List<Mpa> allMpa = mpaDbStorage.getAllMpa();
        assertEquals(allMpa.size(), 5);
    }

    @Test
    public void testIsMpaExist(){
        int id = 10;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> mpaDbStorage.getMpaById(id));
        assertEquals("Mpa id: 10 doesn't exist", exception.getMessage());
    }
}