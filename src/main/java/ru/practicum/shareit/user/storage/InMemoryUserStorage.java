package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;


@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Integer, User> userHashMap = new HashMap<>();

    private Integer id;

    @Override
    public Collection<User> getAll() {
        log.info("Получен список всех пользователей");
        return userHashMap.values();
    }

    @Override
    public User getById(int id) {
        if (userHashMap.containsKey(id)) {
            log.info("Пользователь c id {} найден", id);
            return userHashMap.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public User create(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Указана неверная почта"));
            } else if (userHashMap.containsValue(user)) {
                throw new ValidationException("Пользователь с такой почтой уже существует");
            } else {
                user.setId(makeId());
                userHashMap.put(user.getId(), user);
                log.info("Пользователь c id {} создан", id);
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    @Override
    public void remove(int id) {
        if (userHashMap.containsKey(id)) {
            userHashMap.remove(id);
            log.info("Пользователь c id {} удален", id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", id));
        }
    }


    @Override
    public User update(int id, User user) {
        userHashMap.put(id, user);
        log.info("Пользователь c id {} обновлен", id);
        return user;
    }

    @Override
    public void removeAll() {
        userHashMap.clear();
        log.info("Все пользователи удалены");

    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else if (id < 0) {
            throw new ValidationException("Некорректный id");
        } else {
            id++;
        }
        return id;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return getAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }


}
