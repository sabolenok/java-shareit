package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDateTime;

public class BookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int itemId;
    private BookingStatus status;
}
