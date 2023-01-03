package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemInItemRequest {

    private int id;
    private String name;
    private int userId;
    private String description;
    private boolean available;
    private int requestId;

}
