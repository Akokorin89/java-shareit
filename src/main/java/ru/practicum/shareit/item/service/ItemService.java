package ru.practicum.shareit.item.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class ItemService {

    private ItemStorage itemStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public Collection<Item> getAll() {
        return itemStorage.getAll();
    }

    public Item create(Item item) {
        return itemStorage.create(item);
    }

    public Item getById(int id) {
        return itemStorage.getById(id);
    }

    public void remove(int id) {
        itemStorage.remove(id);
    }

    public Item update(int id, Item item) {
        Item updatedItem = itemStorage.getById(id);
        String updatedName = item.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedItem.setName(updatedName);
        }
        String updateDescription = item.getDescription();
        if (updateDescription != null && !updateDescription.isBlank()) {
            updatedItem.setDescription(updateDescription);
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        itemStorage.update(id, updatedItem);
        return updatedItem;
    }

    public void removeAll() {
        itemStorage.removeAll();
    }

    public List<Item> searchByText(String text) {
        if (text != null && !text.isBlank())
            return itemStorage.searchByText(text.toLowerCase(Locale.ROOT));

        return new ArrayList<>();
    }


    public List<Item> getAllByUser(int userId) {
        return itemStorage.getAll().stream()
                .filter(itemDto -> itemDto.getOwner() == (userId))
                .collect(Collectors.toList());
    }

}