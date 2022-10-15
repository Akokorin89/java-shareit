package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    Item getById(long id);

    List<Item> getAllByUser(long userId);

    List<Item> searchByText(String text);

    Comment addCommentToItem(Comment comment);

    List<Comment> findCommentsByItemId(long itemId);
}