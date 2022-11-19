package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item addNewItem(int userId, Item item);
    Item getById(int id);
}
