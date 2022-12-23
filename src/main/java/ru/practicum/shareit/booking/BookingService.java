package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(int userId, Booking booking);

    Booking put(int userId, int id, boolean isApproved);

    Booking getById(int userId, int id);

    List<Booking> getByUserId(int userId, String state);

    Page<Booking> getByUserIdWithPagination(int userId, String state, int from, int size);

    List<Booking> getByOwnerId(int userId, String state);

    Page<Booking> getByOwnerIdWithPagination(int userId, String state, int from, int size);
}
