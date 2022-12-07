package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository repository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking addNewBooking(int userId, Booking booking) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Item> item = itemRepository.findById(booking.getItemId());
            if (item.isPresent()) {
                if (!item.get().getAvailable()) {
                    throw new ItemNotAvailableException("Вещь недоступна для бронирования!");
                }
                List<Booking> bookings = repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(
                        item.get().getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        BookingStatus.APPROVED
                );
                if (item.get().getUserId() == userId) {
                    throw new NotFoundException("Вещь не найдена!");
                }
                if (!bookings.isEmpty()) {
                    throw new ItemNotAvailableException("Вещь недоступна для бронирования!");
                }
                if (booking.getStart().isAfter(booking.getEnd())) {
                    throw new BookingDateException("Дата начала бронирования не может быть позже даты его окончания!");
                }
                if (booking.getStart().isBefore(LocalDateTime.now())) {
                    throw new BookingDateException("Дата начала бронирования не может быть в прошлом!");
                }
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    throw new BookingDateException("Дата окончания бронирования не может в прошлом!");
                }
                booking.setStatus(BookingStatus.WAITING);
                booking.setBooker(user.get());
                booking.setUserId(userId);
                booking.setItem(item.get());
                return repository.save(booking);
            } else {
                throw new NotFoundException("Вещь не найдена!");
            }
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Transactional
    @Override
    public Booking put(int userId, int id, boolean isApproved) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<Booking> foundBooking = repository.findById(id);
        if (foundBooking.isPresent()) {
            Booking booking = foundBooking.get();
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new BookingStatusException("Текущий статус бронирования не позволяет вносить изменения");
            }
            Optional<Item> foundItem = itemRepository.findByIdAndUserId(booking.getItemId(), userId);
            Optional<User> foundBooker = userRepository.findById(booking.getUserId());
            if (foundItem.isPresent() && foundBooker.isPresent()) {
                booking.setItem(foundItem.get());
                booking.setBooker(foundBooker.get());
                booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                return repository.save(booking);
            } else {
                throw new WrongOwnerException("Пользователь не является владельцем вещи");
            }
        } else {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getById(int userId, int id) {
        Optional<Booking> foundBooking = repository.findById(id);
        if (foundBooking.isPresent()) {
            Booking booking = foundBooking.get();
            Optional<Item> foundItem = itemRepository.findById(booking.getItemId());
            Optional<User> foundBooker = userRepository.findById(booking.getUserId());
            if (foundBooker.isPresent() && foundItem.isPresent()) {
                if (foundItem.get().getUserId() == userId || booking.getUserId() == userId) {
                    booking.setItem(foundItem.get());
                    booking.setBooker(foundBooker.get());
                    return booking;
                } else {
                    throw new WrongOwnerException("У пользователя недостаточно прав для просмотра данного бронирования");
                }
            } else {
                throw new NotFoundException("Вещь не найдена");
            }
        } else {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    @Transactional
    @Override
    public List<Booking> getByUserId(int userId, String requestedState) {

        try {
            State.valueOf(requestedState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + requestedState);
        }

        State state = State.valueOf(requestedState);

        Optional<User> foundBooker = userRepository.findById(userId);
        if (foundBooker.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        User booker = foundBooker.get();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "end"));
                break;
            case CURRENT:
                bookings = repository.findByUserIdAndStartBeforeAndEndAfter(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case PAST:
                bookings = repository.findByUserIdAndEndBefore(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case FUTURE:
                bookings = repository.findByUserIdAndStartAfter(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case WAITING:
                bookings = repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case REJECTED:
                bookings = repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        for (Booking booking : bookings) {
            Optional<Item> foundItem = itemRepository.findById(booking.getItemId());
            foundItem.ifPresent(booking::setItem);
            booking.setBooker(booker);
        }

        return bookings;
    }

    @Transactional
    @Override
    public List<Booking> getByOwnerId(int userId, String requestedState) {

        try {
            State.valueOf(requestedState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + requestedState);
        }

        State state = State.valueOf(requestedState);

        Optional<User> foundOwner = userRepository.findById(userId);
        if (foundOwner.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Item> userItems = itemRepository.findAllByUserIdOrderById(userId);
        if (userItems.isEmpty()) {
            throw new NotFoundException("У пользователь не найдено вещей");
        }

        Set<Integer> itemId = userItems.stream().map(Item::getId).collect(Collectors.toSet());
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdIn(itemId, Sort.by(Sort.Direction.DESC, "end"));
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartBeforeAndEndAfter(
                        itemId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndBefore(
                        itemId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartAfter(
                        itemId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatus(
                        itemId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatus(
                        itemId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "end")
                );
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        for (Booking booking : bookings) {
            Optional<Item> foundItem = itemRepository.findById(booking.getItemId());
            foundItem.ifPresent(booking::setItem);
            Optional<User> foundBooker = userRepository.findById(booking.getUserId());
            foundBooker.ifPresent(booking::setBooker);
        }

        return bookings;
    }
}
