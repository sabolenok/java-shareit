package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.addNewItem(userId, ItemMapper.toItem(itemDto)));
    }

    @GetMapping("/{id}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return ItemMapper.toItemDto(itemService.getById(userId, id));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemDto> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size) {
        return itemService.getAllWithPagination(userId, from, size)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Integer userId,
                         @PathVariable Integer id, @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.put(userId, id, ItemMapper.toItem(itemDto)));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestParam String text) {
        return itemService.search(userId, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search/all")
    public List<ItemDto> searchItemWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam String text,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size) {
        return itemService.searchWithPagination(userId, text, from, size)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return CommentMapper.toCommentDto(itemService.addComment(userId, itemId, CommentMapper.toComment(commentDto)));
    }
}
