package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;


@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User create(User user) {
        userStorage.getByEmail(user.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Пользователь с id %d не найден", user.getId()));
        });
        return userStorage.create(user);
    }

    public void remove(int id) {
        userStorage.remove(id);
    }


    void removeAll() {
        userStorage.removeAll();
    }


    public User update(int id, User user) {
        User updatedUser = userStorage.getById(id);
        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedUser.setName(updatedName);
        }
        String updatedEmail = user.getEmail();

        if (updatedEmail != null && !updatedEmail.isBlank()) {
            userStorage.getByEmail(updatedEmail).ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Пользователь с id %d не найден", id));
            });
            updatedUser.setEmail(updatedEmail);
        }
        return userStorage.update(id, updatedUser);
    }

}
