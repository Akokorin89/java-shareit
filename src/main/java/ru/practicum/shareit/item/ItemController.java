package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(
            @RequestHeader(USER_ID_HEADER) int userId,
            @RequestBody ItemDto itemDto
    ) {
        userService.getById(userId);
        Item item = itemMapper.toItem(itemDto, userId);
        Item itemSaved = itemService.create(item);
        return itemMapper.toItemDto(itemSaved);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(USER_ID_HEADER) int userId,
            @PathVariable int itemId,
            @RequestBody ItemDto itemDto
    ) {
        Item itemValid = itemService.getById(itemId);
        if (itemValid.getOwner() != userId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не пренадлежит владельцу");
        }
        Item item = itemMapper.toItem(itemDto, userId);
        Item itemUpdated = itemService.update(itemId, item);
        return itemMapper.toItemDto(itemUpdated);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        return itemMapper.toItemDto(itemService.getById(id));
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader(USER_ID_HEADER) int userId) {
        return itemService.getAllByUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return itemService.searchByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
