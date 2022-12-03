package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(int userId, Booking booking);

    Booking put(int userId, int id, Booking booking, boolean isApproved);

    Booking getById(int userId, int id);

    List<Booking> getByUserId(int userId, State state);
}
