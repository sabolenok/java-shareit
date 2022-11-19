package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    Item save(Item item);
    Item findById(int id);
}
