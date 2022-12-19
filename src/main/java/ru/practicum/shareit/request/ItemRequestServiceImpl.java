package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private final ItemRequestRepository repository;

    @Autowired
    private final UserRepository userRepository;

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
}
