package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        // modelMapper не может смаппить, т.к. для setItemId соответствует нескольким свойствам источника
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setItemId(bookingDto.getItemId());
        booking.setBooker(bookingDto.getBooker());
        if (bookingDto.getItem() != null) {
            booking.setUserId(bookingDto.getBooker().getId());
        }
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
