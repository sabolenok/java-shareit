package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository repository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public Item addNewItem(int userId, Item item) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            item.setOwner(user.get());
            return repository.save(item);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Item getById(int id) {
        Optional<Item> item = repository.findById(id);
        if (item.isPresent()) {
            Item item1 = item.get();
            setBookingDates(item1);
            return item1;
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(int userId) {
        List<Item> items = repository.findAllByUserId(userId);
        for (Item item : items) {
            setBookingDates(item);
        }
        return items;
    }

    @Transactional
    @Override
    public Item put(int userId, int id, Item item) {
        Optional<Item> foundItem = repository.findById(id);
        if (foundItem.isPresent()) {
            if (foundItem.get().getUserId() != userId) {
                throw new WrongOwnerException("Пользователь не является владельцем вещи");
            }
        }
        setBookingDates(item);
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = repository.findByNameLikeIgnoreCase(text.toLowerCase());
        for (Item item : items) {
            setBookingDates(item);
        }
        return items;
    }

    private void setBookingDates(Item item) {
        Booking last = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        item.setStartOfLastBooking(last.getStart());
        item.setEndOfLastBooking(last.getEnd());
        Booking next = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        item.setStartOfNextBooking(next.getStart());
        item.setEndOfNextBooking(next.getEnd());
    }
}
