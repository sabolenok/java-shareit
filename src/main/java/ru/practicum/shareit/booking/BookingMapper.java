package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    @Autowired
    private final ModelMapper modelMapper;

    public BookingDto toBookingDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    public Booking toBooking(BookingDto bookingDto) {
        return modelMapper.map(bookingDto, Booking.class);
    }
}
