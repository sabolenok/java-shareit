package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addNewItemRequest(int userId, ItemRequest itemRequest);

    List<ItemRequest> getAll(int userId);

    ItemRequest getById(int userId, int id);

    Page<ItemRequest> getAllWithPagination(int userId, int from, int size);

}
