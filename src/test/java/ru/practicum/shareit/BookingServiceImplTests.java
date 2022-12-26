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
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.*;
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
public class BookingServiceImplTests {

    private Booking booking;

    private Item item;

    private User user;

    @Mock
    BookingRepository repository;

    @Mock
    UserRepository userRepo;

    @Mock
    ItemRepository itemRepo;

    BookingServiceImpl bookingService;

    @Test
    public void createNewBookingSuccessful() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(booking);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                        .thenReturn(new ArrayList<>());

        Assertions.assertEquals(bookingService.addNewBooking(1, booking), booking);
    }

    @Test
    public void createNewBookingUserNotFoundThrowsException() {
        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setUserId(1);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void createNewBookingItemNotFoundThrowsException() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingItemNotAvailableThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(false);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (ItemNotAvailableException e) {
            Assertions.assertEquals("Вещь недоступна для бронирования!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingUserIsOwnerThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);
        item.setUserId(1);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingAlreadyApprovedThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(booking);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(List.of(booking));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (ItemNotAvailableException e) {
            Assertions.assertEquals("Вещь недоступна для бронирования!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingStartIsAfterEndThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(30));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(booking);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (BookingDateException e) {
            Assertions.assertEquals("Дата начала бронирования не может быть позже даты его окончания!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingFailedStartDateThrowsException() {
        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(1);
        booking.setItem(item);
        booking.setUserId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(booking);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (BookingDateException e) {
            Assertions.assertEquals("Дата начала бронирования не может быть в прошлом!", e.getMessage());
        }
    }
}
