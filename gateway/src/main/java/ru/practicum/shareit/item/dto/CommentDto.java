package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
