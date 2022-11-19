package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public Item add(@RequestHeader("X-Later-User-Id") Integer userId,
                    @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemService.addNewItem(userId, item);
    }

    @GetMapping("/{id}")
    public Item get(@RequestHeader("X-Later-User-Id") Integer userId, @PathVariable Integer id) {
        return itemService.getById(id);
    }
}
