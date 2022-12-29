package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotBookedItemException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public Item addNewItem(int userId, Item item) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            item.setOwner(user.get());
            item.setUserId(userId);
            return repository.save(item);
        }
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(item.getRequestId());
        itemRequest.ifPresent(item::setRequest);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Item getById(int userId, int id) {
        Optional<Item> foundItem = repository.findById(id);
        if (foundItem.isPresent()) {
            Item item = foundItem.get();
            if (item.getUserId() == userId) {
                setBookingDates(item, null);
            }
            Map<Integer, User> users = getAllUsers();
            setComments(item, users, null);
            return item;
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(int userId) {
        List<Item> items = repository.findAllByUserIdOrderById(userId);
        fillCommentsAndBookingsInItems(userId, items);
        return items;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Item> getAllWithPagination(int userId, int from, int size) {
        Page<Item> items = repository.findAllByUserIdOrderById(userId, PageRequest.of(from / size, size));
        fillCommentsAndBookingsInItems(userId, items);
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
        setBookingDates(item, null);
        Map<Integer, User> users = getAllUsers();
        setComments(item, users, null);
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> search(int userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemsByName = repository.findByNameLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        List<Item> itemsByDescr = repository.findByDescriptionLikeIgnoreCaseAndAvailableOrderById("%" + text.toLowerCase() + "%", true);
        Set<Item> items = new HashSet<>();
        items.addAll(itemsByName);
        items.addAll(itemsByDescr);
        List<Item> sortedItems = items.stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
        fillCommentsAndBookingsInItems(userId, sortedItems);
        return sortedItems;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Item> searchWithPagination(int userId, String text, int from, int size) {
        Page<Item> items = repository.findByNameOrDescriptionNative(text, text, PageRequest.of(from / size, size));
        fillCommentsAndBookingsInItems(userId, items);
        return items;
    }

    @Transactional
    @Override
    public Comment addComment(int userId, int itemId, Comment comment) {
        List<Booking> bookings = bookingRepository.findByItemIdAndUserIdAndEndBeforeAndStatus(
                itemId,
                userId,
                LocalDateTime.now(),
                BookingStatus.APPROVED
        );
        if (bookings == null || bookings.isEmpty()) {
            throw new UserNotBookedItemException("У пользователя не было бронирований этой вещи");
        }
        Optional<Item> foundItem = repository.findById(itemId);
        if (foundItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        } else {
            comment.setItemId(itemId);
            comment.setItem(foundItem.get());
        }
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        } else {
            User user = foundUser.get();
            comment.setAuthorId(userId);
            comment.setAuthorName(user.getName());
            comment.setCreated(LocalDateTime.now());
        }
        return commentRepository.save(comment);
    }

    private void fillCommentsAndBookingsInItems(int userId, List<Item> items) {
        List<Comment> comments = getAllComments();
        List<Booking> bookings = getAllBookings();
        Map<Integer, User> users = getAllUsers();
        for (Item item : items) {
            if (item.getUserId() == userId) {
                setBookingDates(item, bookings);
            }
            setComments(item, users, comments);
        }
    }

    private void fillCommentsAndBookingsInItems(int userId, Page<Item> items) {
        List<Comment> comments = getAllComments();
        List<Booking> bookings = getAllBookings();
        Map<Integer, User> users = getAllUsers();
        for (Item item : items) {
            if (item.getUserId() == userId) {
                setBookingDates(item, bookings);
            }
            setComments(item, users, comments);
        }
    }

    private void setBookingDates(Item item, List<Booking> allBookings) {
        if (allBookings == null) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
            if (lastBooking != null) {
                item.setLastBooking(BookingMapper.toBookingInItem(lastBooking));
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
            if (nextBooking != null) {
                item.setNextBooking(BookingMapper.toBookingInItem(nextBooking));
            }
        } else {
            List<Booking> lastBookings = allBookings.stream()
                            .filter(x -> x.getItemId() == item.getId())
                            .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                            .collect(Collectors.toList());
            if (!lastBookings.isEmpty()) {
                item.setLastBooking(BookingMapper.toBookingInItem(lastBookings.get(0)));
            }
            List<Booking> nextBookings = allBookings.stream()
                    .filter(x -> x.getItemId() == item.getId())
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (!nextBookings.isEmpty()) {
                item.setNextBooking(BookingMapper.toBookingInItem(nextBookings.get(nextBookings.size() - 1)));
            }
        }
    }

    private void setComments(Item item, Map<Integer, User> users, List<Comment> allComments) {
        List<Comment> comments;
        if (allComments == null) {
            comments = commentRepository.findByItemId(item.getId());
        } else {
            comments = allComments.stream().filter(x -> x.getItemId() == item.getId()).collect(Collectors.toList());
        }
        for (Comment c : comments) {
            if (users.containsKey(c.getAuthorId())) {
                c.setAuthorName(users.get(c.getAuthorId()).getName());
            }
        }
        item.setComments(comments);
    }

    private List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    private List<Booking> getAllBookings() {
        return bookingRepository.findAllByStatusOrderByStartDesc(BookingStatus.APPROVED);
    }

    private Map<Integer, User> getAllUsers() {
        Map<Integer, User> foundUsers = new HashMap<>();
        for (User u : userRepository.findAll()) {
            foundUsers.put(u.getId(), u);
        }
        return foundUsers;
    }
}
