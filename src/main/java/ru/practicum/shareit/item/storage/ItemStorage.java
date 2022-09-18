package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAll();

    Item getById(int id);

    Item create(Item item);

    void remove(int id);

    Item update(int id, Item item);

    void removeAll();

    List<Item> searchByText(String text);

}

