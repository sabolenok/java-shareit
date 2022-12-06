package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserIdAndEndBefore(int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByUserId(int bookerId, Sort sort);

    List<Booking> findByUserIdAndStartAfterAndEndBefore(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByUserIdAndStartAfter(int bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByUserIdAndStatus(int bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdIn(Collection<Integer> itemId, Sort sort);

    List<Booking> findByItemIdInAndStartAfterAndEndBefore(Collection<Integer> itemId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemIdInAndEndBefore(Collection<Integer> itemId, LocalDateTime end, Sort sort);

    List<Booking> findByItemIdInAndStartAfter(Collection<Integer> itemId, LocalDateTime start, Sort sort);

    List<Booking> findByItemIdInAndStatus(Collection<Integer> itemId, BookingStatus status, Sort sort);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(int itemId, LocalDateTime start);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    List<Booking> findByItemIdIsAndStartAfterAndEndBeforeAndStatus(int itemId, LocalDateTime start, LocalDateTime end, BookingStatus status);

}
