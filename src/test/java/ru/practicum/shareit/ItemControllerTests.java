package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemServiceImpl itemServiceMock;

    @MockBean
    ItemRepository itemRepository;

    private Item item;

    private ItemDto itemDto;

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

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1);
        item.setDescription("test item description");
        item.setName("test item");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setDescription("test item description");
        itemDto.setName("test item");
        itemDto.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
        item.setUserId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test item request");
        item.setRequestId(itemRequest.getId());

        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);
    }

    //++ Тесты контроллера
    @Test
    void createNewItemTest() throws Exception {
        when(itemServiceMock.addNewItem(anyInt(), any()))
                .thenReturn(item);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void getOneItemTest() throws Exception {
        when(itemServiceMock.getById(anyInt(), anyInt()))
                .thenReturn(item);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemServiceMock.getAll(anyInt()))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItemsWithPaginationTest() throws Exception {
        when(itemServiceMock.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemsWithoutPaginationParamsTest() throws Exception {
        when(itemServiceMock.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.getAll(anyInt()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemsWithoutFirstPaginationParamTest() throws Exception {
        when(itemServiceMock.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.getAll(anyInt()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemsWithoutSecondPaginationParamTest() throws Exception {
        when(itemServiceMock.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.getAll(anyInt()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void updateItemTest() throws Exception {
        item.setDescription("new test item description");
        item.setName("test_item_new");
        when(itemServiceMock.put(anyInt(), anyInt(), any()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value("new test item description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value("test_item_new"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemServiceMock.search(anyInt(), anyString()))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsWithPaginationTest() throws Exception {
        when(itemServiceMock.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItemsWithoutPaginationParamsTest() throws Exception {
        when(itemServiceMock.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.search(anyInt(), anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItemsWithoutFirstPaginationParamTest() throws Exception {
        when(itemServiceMock.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.search(anyInt(), anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItemsWithoutSecondPaginationParamTest() throws Exception {
        when(itemServiceMock.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemServiceMock.search(anyInt(), anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void addCommentTest() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("test comment");

        when(itemServiceMock.addComment(anyInt(), anyInt(), any()))
                .thenReturn(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(commentDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['text']").value(commentDto.getText()));
    }
    //-- Тесты контроллера

    //++ Unit-тесты сервиса
    @Test
    public void createNewItemSuccessful() {
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
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertNull(itemService.addNewItem(1, item));
    }

    @Test
    public void findByIdSuccessful() {
        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());

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
    public void findByIdWithoutBookingDatesSuccessful() {
        Booking lastBooking = new Booking();
        lastBooking.setItemId(item.getId());
        Booking nextBooking = new Booking();
        nextBooking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());

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

        Assertions.assertEquals(itemService.getById(42, 1), item);
    }

    @Test
    public void findByIdNotFoundThrowsException() {
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
    public void findAllWithoutCommentsTest() {
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
        comment.setAuthorId(42);
        comment.setItemId(item.getId());

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
    public void findAllWithoutBookingsTest() {
        Booking lastBooking = new Booking();
        lastBooking.setItemId(42);
        lastBooking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2022, 1, 31, 0, 0));
        Booking nextBooking = new Booking();
        nextBooking.setItemId(42);
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
        Item newItem = new Item();
        newItem.setId(2);
        newItem.setName("test name");
        newItem.setDescription("test description");
        newItem.setAvailable(true);

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
        Assertions.assertEquals(itemService.search(1, "").size(), 0);
    }

    @Test
    public void searchTestWithPagination() {
        Item newItem = new Item();
        newItem.setId(2);
        newItem.setName("test name");
        newItem.setDescription("test description");
        newItem.setAvailable(true);

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
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setName("test name");
        updItem.setDescription("test description");
        updItem.setAvailable(true);

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
    public void updateItemNameDescriptionIsNullSuccessful() {
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setName("test name");

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
    public void updateItemNameDescriptionIsBlankSuccessful() {
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setName("test name");
        updItem.setDescription("");

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
    public void updateItemDescriptionNameIsNullSuccessful() {
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setDescription("new test description");

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
    public void updateItemDescriptionNameIsBlankSuccessful() {
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setDescription("new test description");
        updItem.setName("");

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
        Item updItem = new Item();
        updItem.setId(1);
        updItem.setAvailable(false);

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
        Item updItem = new Item();
        updItem.setId(2);
        updItem.setName("test name");
        updItem.setDescription("test description");
        updItem.setAvailable(true);

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
        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

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
    public void addCommentBookingsNullThrowsException() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

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
    public void addCommentBookingsNullFoundThrowsException() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepo.findByItemIdAndUserIdAndEndBeforeAndStatus(anyInt(), anyInt(), any(), any()))
                .thenReturn(new ArrayList<>());
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
        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

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
        Booking booking = new Booking();
        booking.setItemId(item.getId());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");
        comment.setAuthorId(user.getId());
        comment.setItemId(item.getId());

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

        item.setOwner(user);
        item.setRequest(itemRequest);
        item.setComments(List.of(comment));
        Assertions.assertNotNull(item.getOwner());
        Assertions.assertNotNull(item.getRequest());

        itemDto = ItemMapper.toItemDto(item);

        Item itemNew = ItemMapper.toItem(itemDto);
        itemNew.setUserId(item.getUserId());

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
    //-- Unit-тесты сервиса
}
