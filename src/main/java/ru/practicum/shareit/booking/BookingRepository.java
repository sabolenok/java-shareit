package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserIdAndEndBefore(int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByUserId(int bookerId, Sort sort);

    List<Booking> findByUserIdAndStartAfterAndEndBefore(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByUserIdAndStartAfter(int bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByUserIdAndStatus(int bookerId, BookingStatus status, Sort sort);

}
