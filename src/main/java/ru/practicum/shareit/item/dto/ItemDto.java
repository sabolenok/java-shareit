package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {
    @NotBlank(message = "Наименование не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;
    private BookingInItem lastBooking;
    private BookingInItem nextBooking;
    private List<CommentDto> comments;
    private int id;
}
