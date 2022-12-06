package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public Item addNewItem(int userId, Item item) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            item.setOwner(user.get());
            item.setUserId(userId);
            return repository.save(item);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Item getById(int userId, int id) {
        Optional<Item> foundItem = repository.findById(id);
        if (foundItem.isPresent()) {
            Item item = foundItem.get();
            if (item.getUserId() == userId) {
                setBookingDates(item);
            }
            setComments(item);
            return item;
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(int userId) {
        List<Item> items = repository.findAllByUserIdOrderById(userId);
        for (Item item : items) {
            if (item.getUserId() == userId) {
                setBookingDates(item);
            }
            setComments(item);
        }
        return items;
    }

    @Transactional
    @Override
    public Item put(int userId, int id, Item item) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        User user = foundUser.get();
        Optional<Item> foundItem = repository.findById(id);
        if (foundItem.isPresent()) {
            if (foundItem.get().getUserId() != userId) {
                throw new WrongOwnerException("Пользователь не является владельцем вещи");
            }
            item.setId(id);
            item.setName(
                    (item.getName() == null || item.getName().isBlank())
                            ? foundItem.get().getName()
                            : item.getName()
            );
            item.setDescription(
                    (item.getDescription() == null || item.getDescription().isBlank())
                            ? foundItem.get().getDescription()
                            : item.getDescription()
            );
            item.setUserId(userId);
            item.setOwner(user);
            item.setAvailable(
                    (item.getAvailable() == null)
                            ? foundItem.get().getAvailable()
                            : item.getAvailable()
            );
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
        setBookingDates(item);
        setComments(item);
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Item> search(int userId, String text) {
        if (text.isBlank()) {
            return new HashSet<>();
        }
        List<Item> itemsByName = repository.findByNameLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        List<Item> itemsByDescr = repository.findByDescriptionLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        Set<Item> items = new HashSet<>();
        items.addAll(itemsByName);
        items.addAll(itemsByDescr);
        for (Item item : items) {
            if (item.getUserId() == userId) {
                setBookingDates(item);
            }
            setComments(item);
        }
        return items;
    }

    @Transactional
    @Override
    public Comment addComment(int userId, int itemId, Comment comment) {
        Optional<Item> foundItem = repository.findById(itemId);
        if (foundItem.isPresent()) {
            if (foundItem.get().getUserId() != userId) {
                throw new WrongOwnerException("Пользователь не является владельцем вещи");
            }
        }
        return commentRepository.save(comment);
    }

    private void setBookingDates(Item item) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            item.setLastBooking(bookingMapper.toBookingInItem(lastBooking));
        }
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        if (nextBooking != null) {
            item.setNextBooking(bookingMapper.toBookingInItem(nextBooking));
        }
    }

    private void setComments(Item item) {
        item.setComments(commentRepository.findByItemId(item.getId()));
    }
}
