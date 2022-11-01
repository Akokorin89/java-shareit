package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User getById(long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public User create(User user) {
        log.info("Add user {}", user);
        return repository.save(user);
    }

    @Override
    public void remove(long id) {
        repository.findById(id);
        repository.deleteById(id);
    }

    @Override
    public User update(long userId, User user) {
        User updatedUser = getValidUser(userId, user);
        updatedUser.setId(userId);
        log.info("Updated user {}", updatedUser);

        return repository.save(updatedUser);
    }

    private User getValidUser(long userId, User user) {
        User updatedUser = repository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("User not found"));

        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedUser.setName(updatedName);

        String updatedEmail = user.getEmail();
        if (updatedEmail != null && !updatedEmail.isBlank()) {

            repository.findByEmailContainsIgnoreCase(updatedEmail).ifPresent(u -> {
                throw new ExistsElementException("Email exists");
            });
            updatedUser.setEmail(updatedEmail);
        }
        return updatedUser;
    }

}
