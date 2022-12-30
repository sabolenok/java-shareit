package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toItemRequestDto(
                itemRequestService.addNewItemRequest(userId, ItemRequestMapper.toItemRequest(itemRequestDto))
        );
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAll(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size) {
        return itemRequestService.getAllWithPagination(userId, from, size)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.getById(userId, id));
    }
}
