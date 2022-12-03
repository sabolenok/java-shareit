package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        return bookingMapper.toBookingDto(bookingService.addNewBooking(userId, bookingMapper.toBooking(bookingDto)));
    }

    @PatchMapping("/{id}")
    public BookingDto put(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @Valid @RequestBody BookingDto bookingDto,
                          @PathVariable int id,
                          @RequestParam boolean approved) {
        return bookingMapper.toBookingDto(bookingService.put(userId, id, bookingMapper.toBooking(bookingDto), approved));
    }
}
