package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.impl.DirectorsStorageImpl;

import java.util.List;

@Service
public class DirectorsService {
    private final DirectorsStorageImpl directorsStorage;

    @Autowired
    public DirectorsService(DirectorsStorageImpl directorsStorage) {
        this.directorsStorage = directorsStorage;
    }

    public List<Director> getAllDirectors() {
        return directorsStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        return directorsStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorsStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorsStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorsStorage.deleteDirector(id);
    }

}
