package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
