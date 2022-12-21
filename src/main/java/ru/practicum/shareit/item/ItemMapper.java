package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    @Autowired
    private final CommentMapper commentMapper;

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        List<CommentDto> commentsDto = new ArrayList<>();
        List<Comment> comments = item.getComments();
        if (!(comments == null)) {
            for (Comment c : comments) {
                commentsDto.add(commentMapper.toCommentDto(c));
            }
            itemDto.setComments(commentsDto);
        }
        itemDto.setDescription(item.getDescription());
        itemDto.setLastBooking(item.getLastBooking());
        itemDto.setNextBooking(item.getNextBooking());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        item.setLastBooking(itemDto.getLastBooking());
        item.setNextBooking(itemDto.getNextBooking());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}
