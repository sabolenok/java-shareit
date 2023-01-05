package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        return BookingMapper.toBookingDto(bookingService.addNewBooking(userId, BookingMapper.toBooking(bookingDto)));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto put(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable int bookingId,
                          @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.put(userId, bookingId, approved));
    }

    @GetMapping("/{id}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int id) {
        return BookingMapper.toBookingDto(bookingService.getById(userId, id));
    }

    @GetMapping()
    public List<BookingDto> getByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestParam(defaultValue = "ALL") String state,
                                      @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size) {
        return bookingService.getByUserId(userId, state, from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(defaultValue = "ALL") String state,
                                       @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size) {
        return bookingService.getByOwnerId(userId, state, from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
