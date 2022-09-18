package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAll();

    User getById(int id);

    User create(User user);

    void remove(int id);

    User update(int id, User user);

    void removeAll();

    Optional<User> getByEmail(String email);

}
