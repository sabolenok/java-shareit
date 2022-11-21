package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    @Autowired
    private final ItemStorage itemStorage;
    @Autowired
    private final UserStorage userStorage;

    @Override
    public Item addNewItem(int userId, Item item) {
        User user = userStorage.findById(userId);
        item.setOwner(user);
        return itemStorage.save(item);
    }

    @Override
    public Item getById(int id) {
        return itemStorage.findById(id);
    }

    @Override
    public List<Item> getAll(int userId) {
        return itemStorage.findAll(userId);
    }

    @Override
    public Item put(int userId, int id, Item item) {
        return itemStorage.put(userId, id, item);
    }

    @Override
    public List<Item> search(String text) {
        return itemStorage.search(text);
    }
}
