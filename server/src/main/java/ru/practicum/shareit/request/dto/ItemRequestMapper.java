package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        //Add items
        List<ItemRequestDto.Item> itemsDto = null;
        if (items != null) {
            itemsDto = items.stream()
                    .map(item -> ItemRequestDto.Item.builder()
                            .id(item.getId())
                            .available(item.getAvailable())
                            .requestId(item.getRequest().getId())
                            .description(item.getDescription())
                            .name(item.getName())
                            .build())
                    .collect(Collectors.toList());
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsDto)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}
