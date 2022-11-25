package ru.practicum.shareit.item;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет не найден");
        }
        return items.get(id);
    }

    @Override
    public List<Item> findAll(int userId) {
        return items.values()
                .stream()
                .filter(x -> userId == x.getOwner().getId())
                .collect(Collectors.toList());
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
        item.setName(
                (item.getName() == null || item.getName().isBlank())
                        ? foundItem.getName()
                        : item.getName()
        );
        item.setDescription(
                (item.getDescription() == null || item.getDescription().isBlank())
                        ? foundItem.getDescription()
                        : item.getDescription()
        );
        item.setOwner(
                (item.getOwner() == null)
                        ? foundItem.getOwner()
                        : item.getOwner()
        );
        item.setAvailable(
                (item.getAvailable() == null)
                        ? foundItem.getAvailable()
                        : item.getAvailable()
        );
        items.put(id, item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String finalText = text.toLowerCase();
        return items.values()
                .stream()
                .filter(x -> x.getName().toLowerCase().contains(finalText) || x.getDescription().toLowerCase().contains(finalText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
