package ru.practicum.shareit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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
    public void findByIdNotFound() {
        itemService = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);

        Mockito.when(userRepo.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            itemService.getById(1, 1);
        } catch  (NotFoundException e) {
            Assertions.assertEquals("Вещь не найдена!", e.getMessage());
        }
    }
}
