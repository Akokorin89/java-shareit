package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void getAllByUser() {
        User user = userService.create(new User(null, "Name", "test@test.ru"));
        Item item = itemService.create(user.getId(), new Item(null, "Клей", "Секундный клей момент",
                true,
                user, null));
        Item item2 = itemService.create(user.getId(), new Item(null, "Клей 2", "Секундный клей момент",
                true, user, null));
        Item item3 = itemService.create(user.getId(), new Item(null, "Клей 3", "Секундный клей момент",
                true, user, null));
        Assertions.assertEquals(3, itemService.getAllByUser(user.getId(), 0, 10).size());
        Assertions.assertEquals(item.getName(), itemService.getAllByUser(user.getId(), 0, 10).get(0).getName());
        Assertions.assertEquals(item2.getName(), itemService.getAllByUser(user.getId(), 0, 10).get(1).getName());
        Assertions.assertEquals(item3.getName(), itemService.getAllByUser(user.getId(), 0, 10).get(2).getName());
    }

    @Test
    public void shouldUpdate() {
        User user = userService.create(new User(null, "Name", "test@test.ru"));
        Item item = itemService.create(user.getId(), new Item(null, "Клей", "Секундный клей момент",
                true,
                user, null));
        Item itemUpdate = itemService.update(user.getId(), item.getId(), new Item(1L, "Клей 2", "Суперклей",
                true, user, null));
        Assertions.assertEquals("Клей 2", itemUpdate.getName());
        Assertions.assertEquals("Суперклей", itemUpdate.getDescription());
    }


}
