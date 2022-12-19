package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingInItem {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int bookerId;
}
