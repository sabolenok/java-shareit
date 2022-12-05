package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
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
    public Item getById(int id) {
        Optional<Item> item = repository.findById(id);
        if (item.isPresent()) {
            Item item1 = item.get();
            setBookingDates(item1);
            setComments(item1);
            return item1;
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(int userId) {
        List<Item> items = repository.findAllByUserId(userId);
        for (Item item : items) {
            setBookingDates(item);
            setComments(item);
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
            item.setOwner(userRepository.findById(userId).get());
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
    public Set<Item> search(String text) {
        if (text.isBlank()) {
            return new HashSet<>();
        }
        List<Item> itemsByName = repository.findByNameLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        List<Item> itemsByDescr = repository.findByDescriptionLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        Set<Item> items = new HashSet<>();
        items.addAll(itemsByDescr);
        items.addAll(itemsByName);
        for (Item item : items) {
            setBookingDates(item);
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
        Booking last = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        if (last != null) {
            item.setStartOfLastBooking(last.getStart());
            item.setEndOfLastBooking(last.getEnd());
        }
        Booking next = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        if (next != null) {
            item.setStartOfNextBooking(next.getStart());
            item.setEndOfNextBooking(next.getEnd());
        }
    }

    private void setComments(Item item) {
        item.setComments(commentRepository.findByItemId(item.getId()));
    }
}
