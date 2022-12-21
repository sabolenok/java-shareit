package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addNewItemRequest(int userId, ItemRequest itemRequest);

    List<ItemRequest> getAll(int userId);

}
