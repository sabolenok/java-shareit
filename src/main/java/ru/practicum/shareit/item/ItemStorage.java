package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item save(Item item);

    Item findById(int id);

    List<Item> findAll(int userId);

    Item put(int userId, int id, Item item);

    List<Item> search(String text);
}
