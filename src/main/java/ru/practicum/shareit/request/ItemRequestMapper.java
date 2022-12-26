package ru.practicum.shareit.request;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return modelMapper.map(itemRequest, ItemRequestDto.class);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return modelMapper.map(itemRequestDto, ItemRequest.class);
    }
}
