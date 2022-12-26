package ru.practicum.shareit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotBookedItemException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTests {

    private Item item;

    private User user;

    private ItemRequest itemRequest;

    @Mock
    ItemRepository repository;

    @Mock
    UserRepository userRepo;

    @Mock
    BookingRepository bookingRepo;

    @Mock
    CommentRepository commentRepo;

    @Mock
    ItemRequestRepository requestRepo;

    ItemServiceImpl itemService;

    @Test
    public void createNewItemSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(item);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(requestRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));

        Assertions.assertEquals(itemService.addNewItem(1, item), item);
    }

    @Test
    public void createNewItemUserNotFound() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertNull(itemService.addNewItem(1, item));
    }

    @Test
    public void findByIdSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepo.findAll())
                        .thenReturn(List.of(user));
        Mockito.when(requestRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(bookingRepo.findFirstByItemIdAndStartBeforeOrderByStartDesc(anyInt(), any()))
                        .thenReturn(lastBooking);
        Mockito.when(bookingRepo.findFirstByItemIdAndStartAfterOrderByStartAsc(anyInt(), any()))
                        .thenReturn(nextBooking);
        Mockito.when(commentRepo.findByItemId(anyInt()))
                        .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.getById(1, 1), item);
    }

    @Test
    public void findByIdNotFoundThrowsException() {
        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена!", e.getMessage());
        }
    }

    @Test
    public void findAllTest() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());
        nextBooking.setStart(LocalDateTime.of(2023, 5, 5, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 5, 31, 0, 0));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(lastBooking);
        bookings.add(nextBooking);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(bookingRepo.findAllByStatusOrderByStartDesc(any()))
                .thenReturn(bookings);
        Mockito.when(commentRepo.findAll())
                        .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.getAll(1).get(0), item);
    }

    @Test
    public void findAllWithPagination() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());
        nextBooking.setStart(LocalDateTime.of(2023, 5, 5, 0, 0));
        nextBooking.setStart(LocalDateTime.of(2023, 5, 31, 0, 0));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(lastBooking);
        bookings.add(nextBooking);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findAllByUserIdOrderById(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(bookingRepo.findAllByStatusOrderByStartDesc(any()))
                .thenReturn(bookings);
        Mockito.when(commentRepo.findAll())
                .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.getAllWithPagination(1, 1, 1).getSize(), 1);
    }

    @Test
    public void searchTest() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item newItem = new Item();
        newItem.setId(2);
        newItem.setName("test name");
        newItem.setDescription("test description");
        newItem.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());
        nextBooking.setStart(LocalDateTime.of(2023, 5, 5, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 5, 31, 0, 0));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(lastBooking);
        bookings.add(nextBooking);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        Mockito.when(repository.findByNameLikeIgnoreCaseAndAvailableOrderById(anyString(), anyBoolean()))
                        .thenReturn(List.of(newItem));
        Mockito.when(repository.findByDescriptionLikeIgnoreCaseAndAvailableOrderById(anyString(), anyBoolean()))
                .thenReturn(List.of(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(bookingRepo.findAllByStatusOrderByStartDesc(any()))
                .thenReturn(bookings);
        Mockito.when(commentRepo.findAll())
                .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.search(1, "test").size(), 2);
        Assertions.assertEquals(itemService.search(1, "test").get(0), item);
        Assertions.assertEquals(itemService.search(1, "test").get(1), newItem);
    }

    @Test
    public void searchTestEmpty() {
        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Assertions.assertEquals(itemService.search(1, "").size(), 0);
    }

    @Test
    public void searchTestWithPagination() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item newItem = new Item();
        newItem.setId(2);
        newItem.setName("test name");
        newItem.setDescription("test description");
        newItem.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());
        nextBooking.setStart(LocalDateTime.of(2023, 5, 5, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 5, 31, 0, 0));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(lastBooking);
        bookings.add(nextBooking);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(newItem);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findByNameOrDescriptionNative(anyString(), any()))
                .thenReturn(new PageImpl<>(items));
        Mockito.when(repository.findByNameLikeIgnoreCaseAndAvailableOrderById(anyString(), anyBoolean()))
                .thenReturn(List.of(newItem));
        Mockito.when(repository.findByDescriptionLikeIgnoreCaseAndAvailableOrderById(anyString(), anyBoolean()))
                .thenReturn(List.of(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(bookingRepo.findAllByStatusOrderByStartDesc(any()))
                .thenReturn(bookings);
        Mockito.when(commentRepo.findAll())
                .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.searchWithPagination(1, "test", 0, 2).getSize(), 2);
    }

    @Test
    public void updateItemSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item updItem = new Item();
        updItem.setId(1);
        updItem.setName("test name");
        updItem.setDescription("test description");
        updItem.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());
        nextBooking.setStart(LocalDateTime.of(2023, 5, 5, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 5, 31, 0, 0));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(lastBooking);
        bookings.add(nextBooking);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(updItem);
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepo.findAllByStatusOrderByStartDesc(any()))
                .thenReturn(bookings);
        Mockito.when(commentRepo.findAll())
                .thenReturn(List.of(comment));

        Assertions.assertEquals(itemService.put(1, 1, updItem), updItem);
    }

    @Test
    public void updateItemNameSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item updItem = new Item();
        updItem.setId(1);
        updItem.setName("test name");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(updItem);
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemService.put(1, 1, updItem).getName(), "test name");
        Assertions.assertEquals(updItem.getDescription(), item.getDescription());
        Assertions.assertEquals(updItem.getAvailable(), item.getAvailable());
    }

    @Test
    public void updateItemDescriptionSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item updItem = new Item();
        updItem.setId(1);
        updItem.setDescription("new test description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(updItem);
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemService.put(1, 1, updItem).getName(), item.getName());
        Assertions.assertEquals(updItem.getDescription(), "new test description");
        Assertions.assertEquals(updItem.getAvailable(), item.getAvailable());
    }

    @Test
    public void updateItemAvailableSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item updItem = new Item();
        updItem.setId(1);
        updItem.setAvailable(false);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(updItem);
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemService.put(1, 1, updItem).getName(), item.getName());
        Assertions.assertEquals(updItem.getDescription(), item.getDescription());
        Assertions.assertEquals(updItem.getAvailable(), false);
    }

    @Test
    public void updateItemUserNotFoundThrowsException() {
        Item updItem = new Item();
        updItem.setId(2);
        updItem.setName("test name");
        updItem.setDescription("test description");
        updItem.setAvailable(true);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemService.put(1, 1, updItem);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void updateItemUserIsNotOwnerThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        Item updItem = new Item();
        updItem.setId(2);
        updItem.setName("test name");
        updItem.setDescription("test description");
        updItem.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.of(updItem));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            itemService.put(1, 1, updItem);
        } catch (WrongOwnerException e) {
            Assertions.assertEquals("Пользователь не является владельцем вещи", e.getMessage());
        }
    }

    @Test
    public void updateItemNotOwnerThrowsException() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            itemService.put(1, 1, item);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена!", e.getMessage());
        }
    }

    @Test
    public void addCommentSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepo.findByItemIdAndUserIdAndEndBeforeAndStatus(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepo.save(any()))
                .thenReturn(comment);

        Assertions.assertEquals(itemService.addComment(1, 1, comment), comment);
    }

    @Test
    public void addCommentBookingNotFoundThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepo.findByItemIdAndUserIdAndEndBeforeAndStatus(anyInt(), anyInt(), any(), any()))
                .thenReturn(null);
        Mockito.when(commentRepo.save(any()))
                .thenReturn(comment);

        try {
            itemService.addComment(1, 1, comment);
        } catch (UserNotBookedItemException e) {
            Assertions.assertEquals("У пользователя не было бронирований этой вещи", e.getMessage());
        }
    }

    @Test
    public void addCommentItemNotFoundThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepo.findByItemIdAndUserIdAndEndBeforeAndStatus(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(booking));

        try {
            itemService.addComment(1, 1, comment);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена", e.getMessage());
        }
    }

    @Test
    public void addCommentUserNotFoundThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(bookingRepo.findByItemIdAndUserIdAndEndBeforeAndStatus(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(booking));

        try {
            itemService.addComment(1, 1, comment);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void mapperTest() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItemId(1);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorId(1);
        comment.setAuthorName("author");
        comment.setText("test comment");

        item = new Item();
        item.setId(1);
        item.setName("test item");
        item.setAvailable(true);
        item.setDescription("test description");
        item.setOwner(user);
        item.setRequest(itemRequest);
        item.setComments(List.of(comment));
        Assertions.assertNull(item.getOwner());
        Assertions.assertNull(item.getRequest());

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Item itemNew = ItemMapper.toItem(itemDto);

        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getLastBooking(), itemDto.getLastBooking());
        Assertions.assertEquals(item.getNextBooking(), itemDto.getNextBooking());
        Assertions.assertEquals(item.getRequestId(), itemDto.getRequestId());

        Assertions.assertEquals(itemNew.getId(), itemDto.getId());
        Assertions.assertEquals(itemNew.getName(), itemDto.getName());
        Assertions.assertEquals(itemNew.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(itemNew.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(itemNew.getLastBooking(), itemDto.getLastBooking());
        Assertions.assertEquals(itemNew.getNextBooking(), itemDto.getNextBooking());
        Assertions.assertEquals(itemNew.getRequestId(), itemDto.getRequestId());
        Assertions.assertEquals(itemDto.getComments().size(), 1);

        ItemInItemRequest itemInItemRequest = new ItemInItemRequest();
        itemInItemRequest.setId(item.getId());
        itemInItemRequest.setName(item.getName());
        itemInItemRequest.setDescription(item.getDescription());
        itemInItemRequest.setAvailable(item.getAvailable());
        itemInItemRequest.setRequestId(item.getRequestId());
        itemInItemRequest.setUserId(item.getUserId());

        Assertions.assertEquals(itemNew.getName(), itemInItemRequest.getName());
        Assertions.assertEquals(itemNew.getDescription(), itemInItemRequest.getDescription());
        Assertions.assertEquals(itemNew.getAvailable(), itemInItemRequest.isAvailable());
        Assertions.assertEquals(itemNew.getRequestId(), itemInItemRequest.getRequestId());
        Assertions.assertEquals(itemNew.getUserId(), itemInItemRequest.getUserId());
        Assertions.assertEquals(itemNew.getId(), itemInItemRequest.getId());

        comment.setItem(item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Comment newComment = CommentMapper.toComment(commentDto);

        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getCreated(), commentDto.getCreated());
        Assertions.assertEquals(comment.getAuthorName(), commentDto.getAuthorName());
        Assertions.assertEquals(comment.getItem(), item);

        Assertions.assertEquals(newComment.getId(), commentDto.getId());
        Assertions.assertEquals(newComment.getText(), commentDto.getText());
        Assertions.assertEquals(newComment.getCreated(), commentDto.getCreated());
        Assertions.assertEquals(newComment.getAuthorName(), commentDto.getAuthorName());
    }
}
