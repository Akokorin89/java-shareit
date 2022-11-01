package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("Create item={}, userId={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update itemId={}, userId={}, itemDto={}", itemId, userId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        log.info("Get itemId={}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
            @Positive @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get items userId{}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(
            @RequestParam String text,
            @RequestHeader(USER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
            @Positive @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Search item by text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @RequestBody CommentDto commentDto,
            @PathVariable long itemId,
            @RequestHeader(USER_ID_HEADER) long userId
    ) {
        log.info("Add comment commentDto={}, itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
