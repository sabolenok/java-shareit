package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public final ItemService itemService;
    @Autowired
    private final ItemMapper itemMapper;
    @Autowired
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemService.addNewItem(userId, itemMapper.toItem(itemDto)));
    }

    @GetMapping("/{id}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return itemMapper.toItemDto(itemService.getById(userId, id));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAll(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemDto> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestParam(required = false) @Min(0) Integer from,
                                              @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        if (from == null || size == null) {
            return itemService.getAll(userId)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemService.getAllWithPagination(userId, from, size)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Integer userId,
                         @PathVariable Integer id, @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemService.put(userId, id, itemMapper.toItem(itemDto)));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestParam String text) {
        return itemService.search(userId, text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search/all")
    public List<ItemDto> searchItemWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam String text,
                                                  @RequestParam(required = false) @Min(0) Integer from,
                                              @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        if (from == null || size == null) {
            return itemService.search(userId, text)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemService.searchWithPagination(userId, text, from, size)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return commentMapper.toCommentDto(itemService.addComment(userId, itemId, commentMapper.toComment(commentDto)));
    }
}
