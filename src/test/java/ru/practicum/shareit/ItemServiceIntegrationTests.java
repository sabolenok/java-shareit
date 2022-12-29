package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceIntegrationTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemServiceImpl service;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepository userRepo;

    @Autowired
    BookingRepository bookingRepo;

    @Autowired
    CommentRepository commentRepo;

    @Autowired
    ItemRequestRepository requestRepo;

    private Item item;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("test_user");
        user.setEmail("test@test.ru");
        userService.create(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("test item request");
        itemRequest.setRequestorId(user.getId());
        itemRequestService.addNewItemRequest(user.getId(), itemRequest);

        item = new Item();
        item.setDescription("test item description");
        item.setName("test item");
        item.setAvailable(true);
        item.setUserId(user.getId());
        item.setRequestId(itemRequest.getId());

        service = new ItemServiceImpl(repository, userRepo, bookingRepo, commentRepo, requestRepo);
    }

    @Test
    public void saveItem() {
        service.addNewItem(user.getId(), item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.userId = :userId", Item.class);
        Item item1 = query.setParameter("userId", user.getId()).getSingleResult();

        assertThat(item1.getId(), notNullValue());
        assertThat(item1.getDescription(), equalTo(item.getDescription()));
        assertThat(item1.getName(), equalTo(item.getName()));
        assertThat(item1.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item1.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    public void updateItem() {
        service.addNewItem(user.getId(), item);

        item.setName("new_test_name");
        service.put(user.getId(), item.getId(), item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.userId = :userId", Item.class);
        Item item1 = query.setParameter("userId", user.getId()).getSingleResult();

        assertThat(item1.getId(), equalTo(item.getId()));
        assertThat(item1.getDescription(), equalTo(item.getDescription()));
        assertThat(item1.getName(), equalTo(item.getName()));
        assertThat(item1.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item1.getRequestId(), equalTo(item.getRequestId()));

        item.setAvailable(false);
        service.put(user.getId(), item.getId(), item);

        query = em.createQuery("Select i from Item i where i.userId = :userId", Item.class);
        item1 = query.setParameter("userId", user.getId()).getSingleResult();

        assertThat(item1.getId(), equalTo(item.getId()));
        assertThat(item1.getDescription(), equalTo(item.getDescription()));
        assertThat(item1.getName(), equalTo(item.getName()));
        assertThat(item1.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item1.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    public void getItemById() {
        service.addNewItem(user.getId(), item);

        Item itm = service.getById(user.getId(), item.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item1 = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(item1.getId(), equalTo(itm.getId()));
        assertThat(item1.getDescription(), equalTo(itm.getDescription()));
        assertThat(item1.getName(), equalTo(itm.getName()));
        assertThat(item1.getAvailable(), equalTo(itm.getAvailable()));
        assertThat(item1.getRequestId(), equalTo(itm.getRequestId()));
    }

    @Test
    public void getAllItems() {
        service.addNewItem(user.getId(), item);

        Item newItem = new Item();
        newItem.setName("second item name");
        newItem.setDescription("another item's description");
        newItem.setAvailable(true);
        service.addNewItem(user.getId(), newItem);

        List<Item> items = service.getAll(user.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i", Item.class);
        List<Item> item1 = query.getResultList();

        assertThat(item1.size(), equalTo(2));
        assertThat(item1.size(), equalTo(items.size()));
    }

    @Test
    public void getAllItemsWithPagination() {
        service.addNewItem(user.getId(), item);

        Item newItem = new Item();
        newItem.setName("second item name");
        newItem.setDescription("another item's description");
        newItem.setAvailable(true);
        service.addNewItem(user.getId(), newItem);

        Page<Item> items = service.getAllWithPagination(user.getId(), 0, 2);

        TypedQuery<Item> query = em.createQuery("Select i from Item i", Item.class);
        List<Item> item1 = query.getResultList();

        assertThat(item1.size(), equalTo(2));
        assertThat(item1.size(), equalTo(items.getSize()));
    }

    @Test
    public void searchItems() {
        service.addNewItem(user.getId(), item);

        Item newItem = new Item();
        newItem.setName("second item name");
        newItem.setDescription("another item's description");
        newItem.setAvailable(true);
        service.addNewItem(user.getId(), newItem);

        List<Item> items = service.search(user.getId(), "test");

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name like :text", Item.class);
        List<Item> itemByName = query.setParameter("text", "%test%").getResultList();

        query = em.createQuery("Select i from Item i where i.description like :text", Item.class);
        List<Item> itemByDescription = query.setParameter("text", "%test%").getResultList();

        Set<Item> items1 = new HashSet<>();
        items1.addAll(itemByName);
        items1.addAll(itemByDescription);

        assertThat(items1.size(), equalTo(1));
        assertThat(items1.size(), equalTo(items.size()));
    }

    @Test
    public void searchItemsWithPagination() {
        service.addNewItem(user.getId(), item);

        Item newItem = new Item();
        newItem.setName("second item name");
        newItem.setDescription("another item's description");
        newItem.setAvailable(true);
        service.addNewItem(user.getId(), newItem);

        Page<Item> items = service.searchWithPagination(user.getId(), "test", 0, 1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name like :text", Item.class);
        List<Item> itemByName = query.setParameter("text", "%test%").getResultList();

        query = em.createQuery("Select i from Item i where i.description like :text", Item.class);
        List<Item> itemByDescription = query.setParameter("text", "%test%").getResultList();

        Set<Item> items1 = new HashSet<>();
        items1.addAll(itemByName);
        items1.addAll(itemByDescription);

        assertThat(items1.size(), equalTo(1));
        assertThat(items1.size(), equalTo(items.getSize()));
    }

    @Test
    public void addComment() throws InterruptedException {
        service.addNewItem(user.getId(), item);

        User booker = new User();
        booker.setName("booker_name");
        booker.setEmail("booker@test.ru");
        userService.create(booker);

        Booking booking = new Booking();
        booking.setUserId(booker.getId());
        booking.setBooker(booker);
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingService.addNewBooking(booker.getId(), booking);
        bookingService.put(user.getId(), booking.getId(), true);

        TimeUnit.SECONDS.sleep(3);

        Comment comment = new Comment();
        comment.setText("test comment");

        service.addComment(booker.getId(), item.getId(), comment);

        Item item1 = service.getById(user.getId(), item.getId());

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c", Comment.class);
        List<Comment> comments = query.getResultList();

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.size(), equalTo(item1.getComments().size()));
    }
}
