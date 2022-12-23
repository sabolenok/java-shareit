package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
                                      @RequestParam(defaultValue = "ALL") String state,
                                      @RequestParam(required = false) @Min(0) Integer from,
                                      @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        if (from == null || size == null) {
            return bookingService.getByUserId(userId, state)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return bookingService.getByUserIdWithPagination(userId, state, from, size)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(defaultValue = "ALL") String state,
                                       @RequestParam(required = false) @Min(0) Integer from,
                                       @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        if (from == null || size == null) {
            return bookingService.getByOwnerId(userId, state)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return bookingService.getByOwnerIdWithPagination(userId, state, from, size)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }
}
