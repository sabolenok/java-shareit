package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    @Autowired
    private final ItemRequestService itemRequestService;

    @Autowired
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestMapper.toItemRequestDto(
                itemRequestService.addNewItemRequest(userId, itemRequestMapper.toItemRequest(itemRequestDto))
        );
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAll(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
