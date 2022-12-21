package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemInItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private int id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemInItemRequest> items;
}
