package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto.ItemRequest itemRequest = new ItemDto.ItemRequest();
        if (item.getRequest() != null)
            itemRequest.setId(item.getRequest().getId());
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(itemRequest)
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

    public static Item toItem(User user, ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }
}
