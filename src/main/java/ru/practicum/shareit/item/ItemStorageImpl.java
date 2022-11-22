package ru.practicum.shareit.item;

import lombok.Getter;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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

    @Override
    public List<Item> findAll(int userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (userId == item.getOwner().getId()) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Item put(int userId, int id, Item item) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь не найдена!");
        }
        Item foundItem = items.get(id);
        if (foundItem.getOwner().getId() != userId) {
            throw new WrongOwnerException("Пользователь не является владельцем вещи");
        }
        item.setId(id);
        String name = (item.getName() == null || item.getName().isBlank()) ? foundItem.getName() : item.getName();
        item.setName(name);
        String description = (item.getDescription() == null || item.getDescription().isBlank()) ? foundItem.getDescription() : item.getDescription();
        item.setDescription(description);
        User owner = (item.getOwner() == null) ? foundItem.getOwner() : item.getOwner();
        Boolean available = (item.getAvailable() == null) ? foundItem.getAvailable() : item.getAvailable();
        item.setAvailable(available);
        item.setOwner(owner);
        items.put(id, item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        text = text.toLowerCase();
        List<Item> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)
            && item.getAvailable()) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }
}
