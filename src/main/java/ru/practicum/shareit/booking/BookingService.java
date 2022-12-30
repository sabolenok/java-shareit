package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;

public interface BookingService {
    Booking addNewBooking(int userId, Booking booking);

    Booking put(int userId, int id, boolean isApproved);

    Booking getById(int userId, int id);

    Page<Booking> getByUserIdWithPagination(int userId, String state, int from, int size);

    Page<Booking> getByOwnerIdWithPagination(int userId, String state, int from, int size);
}
