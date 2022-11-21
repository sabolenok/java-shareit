package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(int userId, Item item);
    Item getById(int id);
    List<Item> getAll(int userId);
    Item put(int userId, int id, Item item);
    List<Item> search(String text);
}
