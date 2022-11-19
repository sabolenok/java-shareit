package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemStorage itemStorage;
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
}
