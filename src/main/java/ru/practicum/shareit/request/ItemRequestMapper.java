package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    @Autowired
    private final ModelMapper modelMapper;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return modelMapper.map(itemRequest, ItemRequestDto.class);
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return modelMapper.map(itemRequestDto, ItemRequest.class);
    }
}
