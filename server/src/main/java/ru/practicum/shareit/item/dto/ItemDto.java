package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItem;

import java.util.List;

@Data
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private BookingInItem lastBooking;
    private BookingInItem nextBooking;
    private List<CommentDto> comments;
    private int requestId;
    private int id;
}
