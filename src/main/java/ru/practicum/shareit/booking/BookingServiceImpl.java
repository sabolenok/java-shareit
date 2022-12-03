package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
            booking.setStatus(BookingStatus.WAITING);
            return repository.save(booking);
        }
        return null;
    }

    @Transactional
    @Override
    public Booking put(int userId, int id, Booking booking, boolean isApproved) {
        Optional<Booking> foundBooking = repository.findById(id);
        if (foundBooking.isPresent()) {
            Optional<Item> foundItem = itemRepository.findByIdAndUserId(foundBooking.get().getItemId(), userId);
            if (foundItem.isPresent()) {
                booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                return repository.save(booking);
            } else {
                throw new WrongOwnerException("Пользователь не является владельцем вещи");
            }
        }
        return null;
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
                return repository.findByUserId(userId, Sort.by(Sort.Direction.ASC, "endDate"));
            case CURRENT:
                return repository.findByUserIdAndStartAfterAndEndBefore(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.ASC, "endDate")
                );
            case PAST:
                return repository.findByUserIdAndEndBefore(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.ASC, "endDate")
                );
            case FUTURE:
                return repository.findByUserIdAndStartAfter(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.ASC, "endDate")
                );
            case WAITING:
                return repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.ASC, "endDate")
                );
            case REJECTED:
                return repository.findByUserIdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.ASC, "endDate")
                );
            default:
                return null;
        }
    }
}
