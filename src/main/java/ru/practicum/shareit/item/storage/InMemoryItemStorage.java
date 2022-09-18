package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Item> itemHashMap = new HashMap<>();
    private Integer id = 0;


    @Override
    public List<Item> getAll() {
        log.info("Получен список всех предметов");
        return new ArrayList<>(itemHashMap.values());
    }

    @Override
    public Item getById(int id) {
        log.info("Предмет c id {} найден", id);
        return itemHashMap.get(id);
    }

    @Override
    public Item create(Item item) {
        if (item.getName().isEmpty() || (item.getAvailable() == null) || item.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Плохая вещь"));
        } else {
            item.setId(makeId());
            log.info("Предмет c id {} создан", id);
            itemHashMap.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public void remove(int id) {
        log.info("Предмет c id {} удален", id);
        itemHashMap.remove(id);

    }

    @Override
    public Item update(int id, Item item) {
        itemHashMap.put(id, item);
        log.info("Предмет c id {} обновлен", id);
        return item;

    }

    @Override
    public void removeAll() {
        itemHashMap.clear();
        log.info("Все предметы удалены");

    }

    public List<Item> searchByText(String text) {
        return getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(text) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(text))
                .collect(Collectors.toList());
    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else if (id < 0) {
            throw new ValidationException("Некорректный id");
        } else {
            id++;
        }
        return id;
    }
}
