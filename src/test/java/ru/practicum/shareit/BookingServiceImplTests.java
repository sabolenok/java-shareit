package ru.practicum.shareit;

/*import org.junit.Test;
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
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingInItem;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)*/
public class BookingServiceImplTests {

    /*private Booking booking;

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

    @Test
    public void updateBookingIsApprovedSuccessful() {
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

        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.APPROVED);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(bookingUpd);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.put(1, 1, true), bookingUpd);
        Assertions.assertEquals(booking.getStatus(), bookingUpd.getStatus());
    }

    @Test
    public void updateBookingNotApprovedSuccessful() {
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

        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(bookingUpd);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.put(1, 1, false), bookingUpd);
        Assertions.assertEquals(booking.getStatus(), bookingUpd.getStatus());
    }

    @Test
    public void updateBookingUserNotFoundThrowsException() {
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
                .thenReturn(Optional.empty());

        try {
            bookingService.put(1, 1, true);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void updateBookingNotFoundThrowsException() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.put(1, 1, true);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Бронирование не найдено", e.getMessage());
        }
    }

    @Test
    public void updateBookingStatusIsNotWaitingThrowsException() {
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
        booking.setStatus(BookingStatus.REJECTED);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (BookingStatusException e) {
            Assertions.assertEquals("Текущий статус бронирования не позволяет вносить изменения", e.getMessage());
        }
    }

    @Test
    public void updateBookingUserNotOwnerThrowsException() {
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

        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.save(any()))
                .thenReturn(bookingUpd);
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("Пользователь не является владельцем вещи", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdSuccessful() {
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

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.getById(1, 1), booking);
    }

    @Test
    public void getBookingByIdNotFoundThrowsException() {
        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Бронирование не найдено", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdItemNotFoundThrowsException() {
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

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdUserIsNotOwnerThrowsException() {
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
        booking.setUserId(2);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("У пользователя недостаточно прав для просмотра данного бронирования", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdSuccessful() {
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

        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findAll())
                .thenReturn(List.of(item));
        Mockito.when(repository.findByUserIdOrderByEndDesc(anyInt()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByUserIdAndStartBeforeAndEndAfterOrderByEndDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByUserIdAndEndBeforeOrderByEndDesc(anyInt(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByUserIdAndStartAfterOrderByEndDesc(anyInt(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByUserIdAndStatusOrderByEndDesc(anyInt(), any()))
                .thenReturn(List.of(booking));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByUserId(1, s).get(0), booking);
        }
    }

    @Test
    public void getBookingByUserIdUnknownStateThrowsException() {
        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        try {
            bookingService.getByUserId(1, "wrong_state");
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: wrong_state", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdUserNotFoundThrowsException() {
        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getByUserId(1, "ALL");
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdWithPaginationSuccessful() {
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

        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepo.findAll())
                .thenReturn(List.of(item));
        Mockito.when(repository.findByUserId(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByUserIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByUserIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByUserIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByUserIdAndStatus(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByUserIdWithPagination(1, s, 0, 2).getSize(), 1);
        }
    }

    @Test
    public void getBookingByOwnerIdSuccessful() {
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

        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(itemRepo.findAll())
                .thenReturn(List.of(item));
        Mockito.when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        Mockito.when(repository.findByItemIdInOrderByEndDesc(any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByItemIdInAndStartBeforeAndEndAfterOrderByEndDesc(any(), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByItemIdInAndEndBeforeOrderByEndDesc(any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByItemIdInAndStartAfterOrderByEndDesc(any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(repository.findByItemIdInAndStatusOrderByEndDesc(any(), any()))
                .thenReturn(List.of(booking));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByOwnerId(1, s).get(0), booking);
        }
    }

    @Test
    public void getBookingByOwnerIdWithWrongStateThrowsException() {
        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        try {
            bookingService.getByOwnerId(1, "wrong_state");
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: wrong_state", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdUserNotFoundThrowsException() {
        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getByOwnerId(1, "ALL");
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerItemsNotFoundThrowsException() {
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
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.getByOwnerId(1, "ALL");
        } catch (NotFoundException e) {
            Assertions.assertEquals("У пользователь не найдено вещей", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdWithPaginationSuccessful() {
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

        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        bookingService = new BookingServiceImpl(repository, userRepo, itemRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepo.findAll())
                .thenReturn(List.of(user));
        Mockito.when(itemRepo.findAll())
                .thenReturn(List.of(item));
        Mockito.when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        Mockito.when(repository.findByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByItemIdInAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByItemIdInAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByItemIdInAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findByItemIdInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByOwnerIdWithPagination(1, s, 0, 2).getSize(), 1);
        }
    }

    @Test
    public void bookingInItemMapperTest() {
        booking = new Booking();
        booking.setId(10);
        booking.setStart(LocalDateTime.of(2020, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2021, 1, 1, 0, 0));
        booking.setUserId(42);

        BookingInItem bII = new BookingInItem();
        bII.setId(1);
        bII.setStart(LocalDateTime.now());
        bII.setEnd(LocalDateTime.now());
        bII.setBookerId(1);

        booking.setId(bII.getId());
        booking.setStart(bII.getStart());
        booking.setEnd(bII.getEnd());
        booking.setUserId(bII.getBookerId());

        Assertions.assertEquals(booking.getId(), bII.getId());
        Assertions.assertEquals(booking.getStart(), bII.getStart());
        Assertions.assertEquals(booking.getEnd(), bII.getEnd());
        Assertions.assertEquals(booking.getUserId(), bII.getBookerId());
    }*/
}
