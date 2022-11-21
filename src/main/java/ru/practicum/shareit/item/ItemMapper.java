package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    @Autowired
    private final ModelMapper modelMapper;
    public ItemDto toItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item toItem(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
}
