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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestServiceImpl itemRequestServiceMock;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    private User user;

    @Mock
    ItemRequestRepository repository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test request description");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("test request description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);
    }

    //++ Тесты контроллера
    @Test
    void createNewItemRequestTest() throws Exception {
        when(itemRequestServiceMock.addNewItemRequest(anyInt(), any()))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemRequestDto.getDescription()));
    }

    @Test
    void getOneItemRequestTest() throws Exception {
        when(itemRequestServiceMock.getById(anyInt(), anyInt()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemRequestDto.getDescription()));
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        when(itemRequestServiceMock.getAllForUser(anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getAllItemRequestsWithPaginationTest() throws Exception {
        when(itemRequestServiceMock.getAllOthersUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemRequestsWithoutPaginationParamsTest() throws Exception {
        when(itemRequestServiceMock.getAllOthersUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRequestServiceMock.getAllForUser(anyInt()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemRequestsWithoutFirstPaginationParamTest() throws Exception {
        when(itemRequestServiceMock.getAllOthersUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRequestServiceMock.getAllForUser(anyInt()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemRequestsWithoutSecondPaginationParamTest() throws Exception {
        when(itemRequestServiceMock.getAllOthersUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRequestServiceMock.getAllForUser(anyInt()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    //-- Тесты контроллера

    //++ Unit-тесты сервиса
    @Test
    public void createNewItemRequestSuccessful() {
        Mockito.when(repository.save(any()))
                .thenReturn(itemRequest);
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.addNewItemRequest(1, itemRequest), itemRequest);
    }

    @Test
    public void createNewItemRequestUserNotFound() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertNull(itemRequestService.addNewItemRequest(1, itemRequest));
    }

    @Test
    public void getAllItemRequestsSuccessful() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.getAllForUser(user.getId()).size(), 1);
    }

    @Test
    public void getAllItemRequestsAllItemsIsEmptySuccessful() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAll())
                .thenReturn(new ArrayList<>());

        Assertions.assertEquals(itemRequestService.getAllForUser(user.getId()).size(), 1);
    }

    @Test
    public void getAllItemRequestsAllItemsIsNullSuccessful() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAll())
                .thenReturn(null);

        Assertions.assertEquals(itemRequestService.getAllForUser(user.getId()).size(), 1);
    }

    @Test
    public void getAllItemRequestsUserNotFoundThrowsException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemRequestService.getAllForUser(1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void getByIdItemRequestsSuccessful() {
        Item item = new Item();
        item.setId(1);
        item.setAvailable(true);
        item.setRequestId(1);
        item.setRequest(itemRequest);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item));

        Assertions.assertEquals(itemRequestService.getById(1, 1), itemRequest);
    }

    @Test
    public void getByIdItemRequestsWithoutItemsSuccessful() {
        Item item = new Item();
        item.setId(1);
        item.setAvailable(true);
        item.setRequestId(42);
        item.setRequest(itemRequest);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item));

        Assertions.assertEquals(itemRequestService.getById(1, 1), itemRequest);
    }

    @Test
    public void getByIdItemRequestsUserNotFoundThrowsException() {
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemRequestService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void getByIdItemRequestsNotFoundThrowsException() {
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            itemRequestService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Запрос не найден!", e.getMessage());
        }
    }

    @Test
    public void getAllItemRequestsWithPaginationSuccessful() {
        Mockito.when(repository.findAllByRequestorIdNotOrderByCreated(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.getAllOthersUsers(1, 1, 1).getSize(), 1);
    }

    @Test
    public void getAllItemRequestsWithPaginationUserNotFoundThrowsException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemRequestService.getAllOthersUsers(1, 1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void mapperTest() {
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestorId(42);
        Assertions.assertEquals(itemRequest.getRequestorId(), 42);

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        ItemRequest itemRequestNew = ItemRequestMapper.toItemRequest(itemRequestDto);

        Assertions.assertEquals(itemRequest.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());

        Assertions.assertEquals(itemRequestNew.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequestNew.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(itemRequestNew.getCreated(), itemRequestDto.getCreated());
    }
    //-- Unit-тесты сервиса
}
