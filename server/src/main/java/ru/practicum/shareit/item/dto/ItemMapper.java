package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null)
            requestId = item.getRequest().getId();
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(List<Comment> commentList,
                                                          Booking lastBooking, Booking nextBooking, Item item) {
        List<ItemDtoWithBooking.Comment> comments = commentList.stream()
                .map(comment -> {
                    ItemDtoWithBooking.Comment dtoComments = new ItemDtoWithBooking.Comment();
                    dtoComments.setId(comment.getId());
                    dtoComments.setText(comment.getText());
                    dtoComments.setAuthorName(comment.getAuthor().getName());
                    dtoComments.setCreated(comment.getCreated());
                    return dtoComments;
                }).collect(Collectors.toList());
        ItemDtoWithBooking.Booking lBooking = new ItemDtoWithBooking.Booking();
        if (lastBooking != null) {
            lBooking.setId(lastBooking.getId());
            lBooking.setBookerId(lastBooking.getBooker().getId());
        } else {
            lBooking = null;
        }
        ItemDtoWithBooking.Booking nBooking = new ItemDtoWithBooking.Booking();
        if (nextBooking != null) {
            nBooking.setId(nextBooking.getId());
            nBooking.setBookerId(nextBooking.getBooker().getId());
        } else {
            nBooking = null;
        }
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lBooking)
                .nextBooking(nBooking)
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
