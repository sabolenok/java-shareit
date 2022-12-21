package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<ItemRequest> itemRequests = repository.findAllByRequestorIdOrderByCreated(userId);
        fillInItemInformation(itemRequests);
        return itemRequests;
    }

    private void fillInItemInformation(List<ItemRequest> itemRequests) {
        List<Item> allItems = itemRepository.findAll();

        for (ItemRequest request : itemRequests) {
            List<Item> items = allItems.stream().filter(x -> x.getRequestId() == request.getId()).collect(Collectors.toList());
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
            request.setItems(shortItems);
        }
    }
}
