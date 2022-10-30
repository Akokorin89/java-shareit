package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constant.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.create(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto
    ) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.update(userId, itemId, item));
    }

    @GetMapping("/{id}")
    public ItemDtoWithBooking getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long id) {
        Item item = itemService.getById(id);
        List<Comment> commentList = itemService.findCommentsByItemId(id);
        // Its owner fill Booking
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingService.findLastBookingByItemId(id);
            Booking nextBooking = bookingService.findNextBookingByItemId(id);
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
        Comment comment = CommentMapper.toComment(userId, itemId, commentDto);
        return CommentMapper.toCommentDto(itemService.addCommentToItem(comment));
    }
}
