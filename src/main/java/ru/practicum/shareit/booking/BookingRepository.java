package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserIdAndEndBefore(int bookerId, LocalDateTime end, Sort sort);

    Page<Booking> findByUserIdAndEndBefore(int bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByUserId(int bookerId, Sort sort);

    Page<Booking> findByUserId(int bookerId, Pageable pageable);

    List<Booking> findByUserIdAndStartBeforeAndEndAfter(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    Page<Booking> findByUserIdAndStartBeforeAndEndAfter(int bookerId, LocalDateTime start, LocalDateTime end,
                                                        Pageable pageable);

    List<Booking> findByUserIdAndStartAfter(int bookerId, LocalDateTime start, Sort sort);

    Page<Booking> findByUserIdAndStartAfter(int bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByUserIdAndStatus(int bookerId, BookingStatus status, Sort sort);

    Page<Booking> findByUserIdAndStatus(int bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdIn(Collection<Integer> itemId, Sort sort);

    Page<Booking> findByItemIdIn(Collection<Integer> itemId, Pageable pageable);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfter(Collection<Integer> itemId, LocalDateTime start,
                                                          LocalDateTime end, Sort sort);

    Page<Booking> findByItemIdInAndStartBeforeAndEndAfter(Collection<Integer> itemId, LocalDateTime start,
                                                          LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndEndBefore(Collection<Integer> itemId, LocalDateTime end, Sort sort);

    Page<Booking> findByItemIdInAndEndBefore(Collection<Integer> itemId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndStartAfter(Collection<Integer> itemId, LocalDateTime start, Sort sort);

    Page<Booking> findByItemIdInAndStartAfter(Collection<Integer> itemId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdInAndStatus(Collection<Integer> itemId, BookingStatus status, Sort sort);

    Page<Booking> findByItemIdInAndStatus(Collection<Integer> itemId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(int itemId, LocalDateTime start);

    List<Booking> findAllByStatusOrderByStartDesc(BookingStatus status);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    List<Booking> findByItemIdIsAndStartAfterAndEndBeforeAndStatus(int itemId, LocalDateTime start, LocalDateTime end, BookingStatus status);

    List<Booking> findByItemIdAndUserIdAndEndBeforeAndStatus(int itemId, int userId, LocalDateTime end, BookingStatus status);

}
