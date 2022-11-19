package ru.practicum.shareit.item;

import lombok.Getter;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemStorageImpl implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    @Getter
    private static int id = 0;

    private static int getNextId() {
        return ++id;
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(int id) {
        return items.get(id);
    }
}
