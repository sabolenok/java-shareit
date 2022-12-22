package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemInItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private final ItemRequestRepository repository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest addNewItemRequest(int userId, ItemRequest itemRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            itemRequest.setRequestor(user.get());
            itemRequest.setRequestorId(userId);
            itemRequest.setCreated(LocalDateTime.now());
            return repository.save(itemRequest);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getAll(int userId) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        List<Item> allItems = getAllItems();
        List<ItemRequest> itemRequests = repository.findAllByRequestorIdOrderByCreated(userId);
        for (ItemRequest itemRequest : itemRequests) {
            fillInItemInformation(itemRequest, allItems);
        }
        return itemRequests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getById(int userId, int id) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        Optional<ItemRequest> foundItemRequest = repository.findById(id);
        if (foundItemRequest.isPresent()) {
            ItemRequest itemRequest = foundItemRequest.get();
            List<Item> allItems = getAllItems();
            fillInItemInformation(itemRequest, allItems);
            return itemRequest;
        } else {
            throw new NotFoundException("Запрос не найден!");
        }
    }

    @Override
    public Page<ItemRequest> getAllWithPagination(int userId, int from, int size) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        List<Item> allItems = getAllItems();
        Page<ItemRequest> itemRequests = repository.findAllByRequestorIdNotOrderByCreated(userId, PageRequest.of(from, size));
        for (ItemRequest itemRequest : itemRequests) {
            fillInItemInformation(itemRequest, allItems);
        }
        return itemRequests;
    }

    private List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    private void fillInItemInformation(ItemRequest itemRequest, List<Item> allItems) {
        List<Item> items = allItems.stream()
                .filter(x -> x.getRequestId() == itemRequest.getId())
                .collect(Collectors.toList());
        List<ItemInItemRequest> shortItems = new ArrayList<>();
        for (Item item : items) {
            ItemInItemRequest shortItem = new ItemInItemRequest();
            shortItem.setId(item.getId());
            shortItem.setName(item.getName());
            shortItem.setUserId(item.getUserId());
            shortItem.setDescription(item.getDescription());
            shortItem.setAvailable(item.getAvailable());
            shortItem.setRequestId(item.getRequestId());
            shortItems.add(shortItem);
        }
        itemRequest.setItems(shortItems);
    }
}
