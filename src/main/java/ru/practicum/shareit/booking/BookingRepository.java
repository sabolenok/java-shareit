package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserIdAndEndBeforeOrderByEndDesc(int bookerId, LocalDateTime end);

    Page<Booking> findByUserIdAndEndBefore(int bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByUserIdOrderByEndDesc(int bookerId);

    Page<Booking> findByUserId(int bookerId, Pageable pageable);

    List<Booking> findByUserIdAndStartBeforeAndEndAfterOrderByEndDesc(int bookerId, LocalDateTime start, LocalDateTime end);

    Page<Booking> findByUserIdAndStartBeforeAndEndAfter(int bookerId, LocalDateTime start, LocalDateTime end,
                                                        Pageable pageable);

    List<Booking> findByUserIdAndStartAfterOrderByEndDesc(int bookerId, LocalDateTime start);

    Page<Booking> findByUserIdAndStartAfter(int bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByUserIdAndStatusOrderByEndDesc(int bookerId, BookingStatus status);

    Page<Booking> findByUserIdAndStatus(int bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdInOrderByEndDesc(Collection<Integer> itemId);

    Page<Booking> findByItemIdIn(Collection<Integer> itemId, Pageable pageable);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByEndDesc(Collection<Integer> itemId, LocalDateTime start,
                                                          LocalDateTime end);

    Page<Booking> findByItemIdInAndStartBeforeAndEndAfter(Collection<Integer> itemId, LocalDateTime start,
                                                          LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndEndBeforeOrderByEndDesc(Collection<Integer> itemId, LocalDateTime end);

    Page<Booking> findByItemIdInAndEndBefore(Collection<Integer> itemId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndStartAfterOrderByEndDesc(Collection<Integer> itemId, LocalDateTime start);

    Page<Booking> findByItemIdInAndStartAfter(Collection<Integer> itemId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdInAndStatusOrderByEndDesc(Collection<Integer> itemId, BookingStatus status);

    Page<Booking> findByItemIdInAndStatus(Collection<Integer> itemId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(int itemId, LocalDateTime start);

    List<Booking> findAllByStatusOrderByStartDesc(BookingStatus status);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    List<Booking> findByItemIdIsAndStartAfterAndEndBeforeAndStatus(int itemId, LocalDateTime start, LocalDateTime end, BookingStatus status);

    List<Booking> findByItemIdAndUserIdAndEndBeforeAndStatus(int itemId, int userId, LocalDateTime end, BookingStatus status);

}
