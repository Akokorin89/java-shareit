package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    public void findAll() {
        User user = userService.create(new User(null, "Name", "test@test.ru"));
        User someUser = userService.create(new User(null, "Name 2", "test2@test2.ru"));
        itemRequestService.create(new ItemRequest(null, "Нужен Клей", user, LocalDateTime.now()));
        itemRequestService.create(new ItemRequest(null, "Нужен Молоток", someUser, LocalDateTime.now()));
        itemRequestService.create(new ItemRequest(null, "Нужна дрель", user, LocalDateTime.now()));
        Assertions.assertEquals(1, itemRequestService.findAll(user.getId(), 0, 10).size());
        Assertions.assertEquals(2, itemRequestService.findAll(someUser.getId(), 0, 10).size());
    }
}
