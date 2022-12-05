package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
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
            Optional<Item> foundItem = itemRepository.findByIdAndUserId(booking.getItemId(), userId);
            Optional<User> foundOwner = userRepository.findById(booking.getUserId());
            if (foundItem.isPresent() && foundOwner.isPresent()) {
                booking.setItem(foundItem.get());
                booking.setBooker(foundOwner.get());
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
            Optional<Item> foundItem = itemRepository.findByIdAndUserId(foundBooking.get().getItemId(), userId);
            if (foundItem.isPresent() || foundBooking.get().getUserId() == userId) {
                return foundBooking.get();
            } else {
                throw new WrongOwnerException("У пользователя недостаточно прав для просмотра данного бронирования");
            }
        }
        return null;
    }

    @Transactional
    @Override
    public List<Booking> getByUserId(int userId, State state) {
        switch (state) {
            case ALL:
                return repository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "endDate"));
            case CURRENT:
                return repository.findByUserIdAndStartAfterAndEndBefore(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case PAST:
                return repository.findByUserIdAndEndBefore(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case FUTURE:
                return repository.findByUserIdAndStartAfter(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case WAITING:
                return repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case REJECTED:
                return repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            default:
                return null;
        }
    }

    @Transactional
    @Override
    public List<Booking> getByOwnerId(int userId, State state) {
        List<Item> userItems = itemRepository.findAllByUserId(userId);
        if (userItems.isEmpty()) {
            return null;
        }
        Set<Integer> itemId = userItems.stream().map(Item::getId).collect(Collectors.toSet());
        switch (state) {
            case ALL:
                return repository.findByItemIdIn(itemId, Sort.by(Sort.Direction.DESC, "endDate"));
            case CURRENT:
                return repository.findByItemIdInAndStartAfterAndEndBefore(
                        itemId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case PAST:
                return repository.findByItemIdInAndEndBefore(
                        itemId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case FUTURE:
                return repository.findByItemIdInAndStartAfter(
                        itemId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case WAITING:
                return repository.findByItemIdInAndStatus(
                        itemId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            case REJECTED:
                return repository.findByItemIdInAndStatus(
                        itemId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "endDate")
                );
            default:
                return null;
        }
    }
}
