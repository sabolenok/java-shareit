package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.exception.UnsupportedStateException;
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

        State state = getRequestedState(requestedState);
        User booker = checkUser(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByUserIdOrderByEndDesc(userId);
                break;
            case CURRENT:
                bookings = repository.findByUserIdAndStartBeforeAndEndAfterOrderByEndDesc(userId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.findByUserIdAndEndBeforeOrderByEndDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.findByUserIdAndStartAfterOrderByEndDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.findByUserIdAndStatusOrderByEndDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByUserIdAndStatusOrderByEndDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        List<Item> allItems = getAllItems();
        for (Booking booking : bookings) {
            fillItem(booking, allItems);
            booking.setBooker(booker);
        }

        return bookings;
    }

    @Transactional
    @Override
    public Page<Booking> getByUserIdWithPagination(int userId, String requestedState, int from, int size) {

        State state = getRequestedState(requestedState);
        User booker = checkUser(userId);
        Page<Booking> bookings;
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "end"));

        switch (state) {
            case ALL:
                bookings = repository.findByUserId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = repository.findByUserIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = repository.findByUserIdAndEndBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = repository.findByUserIdAndStartAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = repository.findByUserIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = repository.findByUserIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        List<Item> allItems = getAllItems();
        for (Booking booking : bookings) {
            fillItem(booking, allItems);
            booking.setBooker(booker);
        }

        return bookings;
    }

    @Transactional
    @Override
    public List<Booking> getByOwnerId(int userId, String requestedState) {

        State state = getRequestedState(requestedState);

        checkUser(userId);

        List<Item> userItems = checkUserItems(userId);

        Set<Integer> itemId = userItems.stream().map(Item::getId).collect(Collectors.toSet());
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdInOrderByEndDesc(itemId);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartBeforeAndEndAfterOrderByEndDesc(itemId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartAfterOrderByEndDesc(itemId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatusOrderByEndDesc(itemId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatusOrderByEndDesc(itemId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        List<Item> allItems = getAllItems();
        List<User> allUsers = getAllUsers();
        for (Booking booking : bookings) {
            fillItem(booking, allItems);
            fillUser(booking, allUsers);
        }

        return bookings;
    }

    @Transactional
    @Override
    public Page<Booking> getByOwnerIdWithPagination(int userId, String requestedState, int from, int size) {

        State state = getRequestedState(requestedState);

        checkUser(userId);
        List<Item> userItems = checkUserItems(userId);

        Set<Integer> itemId = userItems.stream().map(Item::getId).collect(Collectors.toSet());
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "end"));
        Page<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdIn(itemId, pageRequest);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartBeforeAndEndAfter(itemId, LocalDateTime.now(),
                        LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndBefore(itemId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartAfter(itemId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatus(itemId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatus(itemId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        List<Item> allItems = getAllItems();
        List<User> allUsers = getAllUsers();
        for (Booking booking : bookings) {
            fillItem(booking, allItems);
            fillUser(booking, allUsers);
        }

        return bookings;
    }

    private State getRequestedState(String requestedState) {
        try {
            State.valueOf(requestedState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + requestedState);
        }

        return State.valueOf(requestedState);
    }

    private User checkUser(int userId) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return foundUser.get();
    }

    private List<Item> checkUserItems(int userId) {
        List<Item> userItems = itemRepository.findAllByUserIdOrderById(userId);
        if (userItems.isEmpty()) {
            throw new NotFoundException("У пользователь не найдено вещей");
        }
        return userItems;
    }

    private List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    private List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private void fillItem(Booking booking, List<Item> allItems) {
        Optional<Item> foundItem = allItems.stream().filter(x -> x.getId() == booking.getItemId()).findFirst();
        foundItem.ifPresent(booking::setItem);
    }

    private void fillUser(Booking booking, List<User> allUsers) {
        Optional<User> foundBooker = allUsers.stream().filter(x -> x.getId() == booking.getUserId()).findFirst();
        foundBooker.ifPresent(booking::setBooker);
    }
}
