package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private int requestId;
}
