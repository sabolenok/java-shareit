package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInItem;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingServiceImpl bookingServiceMock;

    @MockBean
    BookingRepository bookingRepository;

    private Booking booking;

    private BookingDto bookingDto;

    private Item item;

    private User user;

    @Mock
    BookingRepository repository;

    @Mock
    UserRepository userRepo;

    @Mock
    ItemRepository itemRepo;

    BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
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

        bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setStatus(BookingStatus.WAITING);

        item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
    }

    //++ ?????????? ??????????????????????
    @Test
    void createNewBookingTest() throws Exception {
        when(bookingServiceMock.addNewBooking(anyInt(), any()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(bookingDto.getStatus().name()));
    }

    @Test
    void getOneBookingTest() throws Exception {
        when(bookingServiceMock.getById(anyInt(), anyInt()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(bookingDto.getStatus().name()));
    }

    @Test
    void updateBookingTest() throws Exception {
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingServiceMock.put(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "false")
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(BookingStatus.REJECTED.name()));
    }

    @Test
    void getBookingsByUserWithPaginationTest() throws Exception {
        when(bookingServiceMock.getByUserId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByUserWithoutPaginationParamsTest() throws Exception {
        when(bookingServiceMock.getByUserId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByUserWithoutFirstPaginationParamTest() throws Exception {
        when(bookingServiceMock.getByUserId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByUserWithoutSecondPaginationParamTest() throws Exception {
        when(bookingServiceMock.getByUserId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwnerWithPaginationTest() throws Exception {
        when(bookingServiceMock.getByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwnerWithoutPaginationParamsTest() throws Exception {
        when(bookingServiceMock.getByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwnerWithoutFirstPaginationParamTest() throws Exception {
        when(bookingServiceMock.getByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwnerWithoutSecondPaginationParamTest() throws Exception {
        when(bookingServiceMock.getByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }
    //-- ?????????? ??????????????????????

    //++ Unit-?????????? ??????????????
    @Test
    public void createNewBookingSuccessful() {
        when(repository.save(any()))
                .thenReturn(booking);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        Assertions.assertEquals(bookingService.addNewBooking(1, booking), booking);
    }

    @Test
    public void createNewBookingUserNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ????????????", e.getMessage());
        }
    }

    @Test
    public void createNewBookingItemNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("???????? ???? ??????????????!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingItemNotAvailableThrowsException() {
        item.setAvailable(false);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (ItemNotAvailableException e) {
            Assertions.assertEquals("???????? ???????????????????? ?????? ????????????????????????!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingUserIsOwnerThrowsException() {
        item.setUserId(1);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("???????? ???? ??????????????!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingAlreadyApprovedThrowsException() {
        when(repository.save(any()))
                .thenReturn(booking);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(List.of(booking));

        try {
            bookingService.addNewBooking(1, booking);
        } catch (ItemNotAvailableException e) {
            Assertions.assertEquals("???????? ???????????????????? ?????? ????????????????????????!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingStartIsAfterEndThrowsException() {
        booking.setStart(LocalDateTime.now().plusMinutes(30));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));

        when(repository.save(any()))
                .thenReturn(booking);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (BookingDateException e) {
            Assertions.assertEquals("???????? ???????????? ???????????????????????? ???? ?????????? ???????? ?????????? ???????? ?????? ??????????????????!", e.getMessage());
        }
    }

    @Test
    public void createNewBookingFailedStartDateThrowsException() {
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));

        when(repository.save(any()))
                .thenReturn(booking);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findByItemIdIsAndStartAfterAndEndBeforeAndStatus(anyInt(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.addNewBooking(1, booking);
        } catch (BookingDateException e) {
            Assertions.assertEquals("???????? ???????????? ???????????????????????? ???? ?????????? ???????? ?? ??????????????!", e.getMessage());
        }
    }

    @Test
    public void updateBookingIsApprovedSuccessful() {
        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.APPROVED);

        when(repository.save(any()))
                .thenReturn(bookingUpd);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.put(1, 1, true), bookingUpd);
        Assertions.assertEquals(booking.getStatus(), bookingUpd.getStatus());
    }

    @Test
    public void updateBookingNotApprovedSuccessful() {
        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);

        when(repository.save(any()))
                .thenReturn(bookingUpd);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.put(1, 1, false), bookingUpd);
        Assertions.assertEquals(booking.getStatus(), bookingUpd.getStatus());
    }

    @Test
    public void updateBookingUserNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.put(1, 1, true);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ????????????", e.getMessage());
        }
    }

    @Test
    public void updateBookingNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.put(1, 1, true);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ??????????????", e.getMessage());
        }
    }

    @Test
    public void updateBookingStatusIsNotWaitingThrowsException() {
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (BookingStatusException e) {
            Assertions.assertEquals("?????????????? ???????????? ???????????????????????? ???? ?????????????????? ?????????????? ??????????????????", e.getMessage());
        }
    }

    @Test
    public void updateBookingUserNotOwnerAndNotBookerThrowsException() {
        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);
        booking.setUserId(42);

        when(repository.save(any()))
                .thenReturn(bookingUpd);
        when(userRepo.findById(1))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findById(42))
                .thenReturn(Optional.empty());
        when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("???????????????????????? ???? ???????????????? ???????????????????? ????????", e.getMessage());
        }
    }

    @Test
    public void updateBookingUserNotOwnerThrowsException() {
        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);

        when(repository.save(any()))
                .thenReturn(bookingUpd);
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("???????????????????????? ???? ???????????????? ???????????????????? ????????", e.getMessage());
        }
    }

    @Test
    public void updateBookingUserNotBookerThrowsException() {
        Booking bookingUpd = new Booking();
        bookingUpd.setId(1);
        bookingUpd.setStart(LocalDateTime.now().plusMinutes(10));
        bookingUpd.setEnd(LocalDateTime.now().plusMinutes(20));
        bookingUpd.setItemId(1);
        bookingUpd.setItem(item);
        bookingUpd.setUserId(1);
        bookingUpd.setBooker(user);
        bookingUpd.setStatus(BookingStatus.REJECTED);
        booking.setUserId(42);

        when(repository.save(any()))
                .thenReturn(bookingUpd);
        when(userRepo.findById(1))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findById(42))
                .thenReturn(Optional.empty());
        when(itemRepo.findByIdAndUserId(anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.put(1, 1, true);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("???????????????????????? ???? ???????????????? ???????????????????? ????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdSuccessful() {
        item.setUserId(1);
        booking.setUserId(1);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.getById(1, 1), booking);
    }

    @Test
    public void getBookingByIdUserIsOwnerSuccessful() {
        item.setUserId(1);
        booking.setUserId(42);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.getById(1, 1), booking);
    }

    @Test
    public void getBookingByIdUserIsBookerSuccessful() {
        item.setUserId(42);
        booking.setUserId(1);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingService.getById(1, 1), booking);
    }

    @Test
    public void getBookingByIdNotFoundThrowsException() {
        when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ??????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdItemNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????? ???? ??????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdBookerNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????? ???? ??????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdItemNotFoundAndBookerNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("???????? ???? ??????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByIdUserIsNotOwnerAndNotBookerThrowsException() {
        item.setUserId(42);
        booking.setUserId(42);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.getById(1, 1);
        } catch  (WrongOwnerException e) {
            Assertions.assertEquals("?? ???????????????????????? ???????????????????????? ???????? ?????? ?????????????????? ?????????????? ????????????????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(repository.findByUserId(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStatus(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByUserId(1, s, 0, 5)
                    .stream().findFirst(), Optional.ofNullable(booking));
        }
    }

    @Test
    public void getBookingByUserIdDontSetItemSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        item.setId(42);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(repository.findByUserId(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStatus(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByUserId(1, s, 0, 5)
                    .stream().findFirst(), Optional.ofNullable(booking));
        }
    }

    @Test
    public void getBookingByUserIdUnknownStateThrowsException() {
        try {
            bookingService.getByUserId(1, "wrong_state", 0, 5);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: wrong_state", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdUnsupportedStateThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            bookingService.getByUserId(1, State.TEST.name(), 0, 5);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: TEST", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdUserNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getByUserId(1, "ALL", 0, 5);
        } catch (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByUserIdWithPaginationSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(repository.findByUserId(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByUserIdAndStatus(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByUserId(1, s, 0, 2).getSize(), 1);
        }
    }

    @Test
    public void getBookingByUserIdWithPaginationUnsupportedStateThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            bookingService.getByUserId(1, State.TEST.name(), 0, 2);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: TEST", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findAll())
                .thenReturn(List.of(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        when(repository.findByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByOwnerId(1, s, 0, 5)
                    .stream().findFirst(), Optional.ofNullable(booking));
        }
    }

    @Test
    public void getBookingByOwnerIdDontSetBookerSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        User userOther = new User();
        userOther.setId(42);

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findAll())
                .thenReturn(List.of(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        when(repository.findByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByOwnerId(1, s, 0, 5)
                    .stream().findFirst(), Optional.ofNullable(booking));
        }
    }

    @Test
    public void getBookingByOwnerIdWithWrongStateThrowsException() {
        try {
            bookingService.getByOwnerId(1, "wrong_state", 0, 5);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: wrong_state", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdUnsupportedStateThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));

        try {
            bookingService.getByOwnerId(1, State.TEST.name(), 0, 5);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: TEST", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdUserNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            bookingService.getByOwnerId(1, "ALL", 0, 5);
        } catch (NotFoundException e) {
            Assertions.assertEquals("???????????????????????? ???? ????????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerItemsNotFoundThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findAll())
                .thenReturn(List.of(user));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(new ArrayList<>());

        try {
            bookingService.getByOwnerId(1, "ALL", 0, 5);
        } catch (NotFoundException e) {
            Assertions.assertEquals("?? ???????????????????????? ???? ?????????????? ??????????", e.getMessage());
        }
    }

    @Test
    public void getBookingByOwnerIdWithPaginationSuccessful() {
        List<String> states = new ArrayList<>();
        states.add(State.ALL.name());
        states.add(State.CURRENT.name());
        states.add(State.PAST.name());
        states.add(State.FUTURE.name());
        states.add(State.WAITING.name());
        states.add(State.REJECTED.name());

        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepo.findAll())
                .thenReturn(List.of(user));
        when(itemRepo.findAll())
                .thenReturn(List.of(item));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));
        when(repository.findByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(repository.findByItemIdInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        for (String s : states) {
            Assertions.assertEquals(bookingService.getByOwnerId(1, s, 0, 2).getSize(), 1);
        }
    }

    @Test
    public void getBookingByOwnerIdWithPaginationUnsupportedStateThrowsException() {
        when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepo.findAllByUserIdOrderById(anyInt()))
                .thenReturn(List.of(item));

        try {
            bookingService.getByOwnerId(1, State.TEST.name(), 0, 2);
        } catch (UnsupportedStateException e) {
            Assertions.assertEquals("Unknown state: TEST", e.getMessage());
        }
    }

    @Test
    public void mapperTest() {
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        Assertions.assertEquals(newBooking.getId(), bookingDto.getId());

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
    }
    //-- Unit-?????????? ??????????????
}
