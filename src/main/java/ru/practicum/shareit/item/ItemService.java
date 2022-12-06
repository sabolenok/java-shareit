package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    Item addNewItem(int userId, Item item);

    Item getById(int userId, int id);

    List<Item> getAll(int userId);

    Item put(int userId, int id, Item item);

    Set<Item> search(int userId, String text);

    Comment addComment(int userId, int itemId, Comment comment);
}
