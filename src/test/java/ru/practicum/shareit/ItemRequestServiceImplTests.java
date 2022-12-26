package ru.practicum.shareit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceImplTests {

    private ItemRequest itemRequest;

    private User user;

    @Mock
    ItemRequestRepository repository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    ItemRequestServiceImpl itemRequestService;

    @Test
    public void createNewItemRequestSuccessful() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.save(any()))
                .thenReturn(itemRequest);
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.addNewItemRequest(1, itemRequest), itemRequest);
    }

    @Test
    public void createNewItemRequestUserNotFound() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertNull(itemRequestService.addNewItemRequest(1, itemRequest));
    }

    @Test
    public void getAllItemRequestsSuccessful() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.findAllByRequestorIdOrderByCreated(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.getAll(user.getId()).size(), 1);
    }

    @Test
    public void getAllItemRequestsUserNotFoundThrowsException() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemRequestService.getAll(1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void getByIdItemRequestsSuccessful() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        Item item = new Item();
        item.setId(1);
        item.setAvailable(true);
        item.setRequestId(1);
        item.setRequest(itemRequest);

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

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
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

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
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

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
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(repository.findAllByRequestorIdNotOrderByCreated(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(itemRequestService.getAllWithPagination(1, 1, 1).getSize(), 1);
    }

    @Test
    public void getAllItemRequestsWithPaginationUserNotFoundThrowsException() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        try {
            itemRequestService.getAllWithPagination(1, 1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден!", e.getMessage());
        }
    }

    @Test
    public void mapperTest() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestorId(42);
        Assertions.assertEquals(itemRequest.getRequestorId(), 42);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        ItemRequest itemRequestNew = ItemRequestMapper.toItemRequest(itemRequestDto);

        Assertions.assertEquals(itemRequest.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());

        Assertions.assertEquals(itemRequestNew.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequestNew.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(itemRequestNew.getCreated(), itemRequestDto.getCreated());
    }
}
