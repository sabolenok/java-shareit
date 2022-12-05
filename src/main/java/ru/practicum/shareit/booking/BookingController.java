package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @PatchMapping("/{bookingId}")
    public BookingDto put(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer bookingId,
                          @RequestParam Boolean approved) {
        return bookingMapper.toBookingDto(bookingService.put(userId, bookingId, approved));
    }

    @GetMapping("/{id}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int id) {
        return bookingMapper.toBookingDto(bookingService.getById(userId, id));
    }

    @GetMapping()
    public List<BookingDto> getByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestParam State state) {
        return bookingService.getByUserId(userId, state)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam State state) {
        return bookingService.getByOwnerId(userId, state)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
