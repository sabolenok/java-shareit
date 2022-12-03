package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository repository;

    @Autowired
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Item addNewItem(int userId, Item item) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            item.setOwner(user.get());
            return repository.save(item);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Item getById(int id) {
        Optional<Item> item = repository.findById(id);
        return item.orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(int userId) {
        return repository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public Item put(int userId, int id, Item item) {
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.findByNameLikeIgnoreCase(text.toLowerCase());
    }
}
