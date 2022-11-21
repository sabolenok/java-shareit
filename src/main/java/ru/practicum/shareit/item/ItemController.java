package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Integer userId,
                    @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemService.addNewItem(userId, item));
    }

    @GetMapping("/{id}")
    public ItemDto get(@RequestHeader("X-Later-User-Id") Integer userId, @PathVariable Integer id) {
        return itemMapper.toItemDto(itemService.getById(id));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Later-User-Id") Integer userId) {
        List<Item> items = itemService.getAll(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Later-User-Id") Integer userId,
                         @PathVariable Integer id, @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemService.put(userId, id, item));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Later-User-Id") Integer userId,
                                    @RequestParam String text) {
        List<Item> items = itemService.search(text);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }
}
