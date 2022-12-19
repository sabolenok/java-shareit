package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private int id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private User requestor;
    private LocalDateTime created;
}
