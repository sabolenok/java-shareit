package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item addNewItem(int userId, Item item);

    Item getById(int userId, int id);

    Page<Item> getAll(int userId, int from, int size);

    Item put(int userId, int id, Item item);

    Page<Item> search(int userId, String text, int from, int size);

    Comment addComment(int userId, int itemId, Comment comment);
}
