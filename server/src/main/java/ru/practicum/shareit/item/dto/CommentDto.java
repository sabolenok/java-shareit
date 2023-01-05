package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    private LocalDateTime created;
    private String authorName;
}
