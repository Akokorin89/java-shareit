package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    Item getById(long id);

    List<Item> getAllByUser(long userId, int from, int size);

    List<Item> findByRequestId(long requestId);

    List<Item> searchByText(String text, int from, int size);

    Comment addCommentToItem(Comment comment);

    List<Comment> findCommentsByItemId(long itemId);
}
