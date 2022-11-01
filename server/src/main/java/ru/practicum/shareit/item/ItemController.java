package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Add item itemDto={}, userId={}", itemDto, userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.create(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update item userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.update(userId, itemId, item));
    }

    @GetMapping("/{id}")
    public ItemDtoWithBooking getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long id) {
        log.info("Get item userId={}, itemId={}", userId, id);
        Item item = itemService.getById(id);
        List<Comment> commentList = itemService.findCommentsByItemId(id);
        // Its owner fill Booking
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingService.findLastBookingByItemId(id);
            log.info("Find lastBooking={}", lastBooking);
            Booking nextBooking = bookingService.findNextBookingByItemId(id);
            log.info("Find nextBooking={}", nextBooking);
            return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
        } else {
            return ItemMapper.toItemDtoWithBooking(commentList, null, null, item);
        }
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllByUser(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get items by user owner userId={}, from={}, size={}", userId, from, size);
        return itemService.getAllByUser(userId, from, size).stream()
                .map(item -> {
                    List<Comment> commentList = itemService.findCommentsByItemId(item.getId());
                    Booking lastBooking = bookingService.findLastBookingByItemId(item.getId());
                    Booking nextBooking = bookingService.findNextBookingByItemId(item.getId());
                    return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(
            @RequestParam String text,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Search items by text text={}, from={}, size={}", text, from, size);
        return itemService.searchByText(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestBody CommentDto commentDto,
            @PathVariable long itemId,
            @RequestHeader(USER_ID_HEADER) long userId
    ) {
        log.info("Add comment to item commentDto={}, itemId={}, userId={}", commentDto, itemId, userId);
        Comment comment = CommentMapper.toComment(userId, itemId, commentDto);
        return CommentMapper.toCommentDto(itemService.addCommentToItem(comment));
    }
}
