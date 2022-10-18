package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constant.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        User user = userService.getById(userId);
        Item item = ItemMapper.toItem(user, itemDto);
        Item itemSaved = itemService.create(userId, item);
        return ItemMapper.toItemDto(itemSaved);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto
    ) {
        User user = userService.getById(userId);
        Item item = ItemMapper.toItem(user, itemDto);
        Item itemUpdated = itemService.update(userId, itemId, item);
        return ItemMapper.toItemDto(itemUpdated);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBooking getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long id) {
        Item item = itemService.getById(id);
        List<Comment> commentList = itemService.findCommentsByItemId(id);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingService.findLastBookingByItemId(id);
            Booking nextBooking = bookingService.findNextBookingByItemId(id);
            return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
        } else {
            return ItemMapper.toItemDtoWithBooking(commentList, null, null, item);
        }
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllByUser(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllByUser(userId).stream()
                .map(item -> {
                    List<Comment> commentList = itemService.findCommentsByItemId(item.getId());
                    Booking lastBooking = bookingService.findLastBookingByItemId(item.getId());
                    Booking nextBooking = bookingService.findNextBookingByItemId(item.getId());
                    return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return itemService.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestBody CommentDto commentDto,
            @PathVariable long itemId,
            @RequestHeader(USER_ID_HEADER) long userId
    ) {
        User author = userService.getById(userId);
        Item item = itemService.getById(itemId);
        Comment comment = CommentMapper.toComment(author, item, commentDto);
        return CommentMapper.toCommentDto(itemService.addCommentToItem(comment));
    }
}
