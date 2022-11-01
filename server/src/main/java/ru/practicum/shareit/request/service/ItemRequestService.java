package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest);

    ItemRequest findById(long id);

    List<ItemRequest> findByRequesterId(long requesterId);

    List<ItemRequest> findAll(long userId, int from, int size);
}
