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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceIntegrationTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private BookingServiceImpl service;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private BookingRepository repository;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ItemRepository itemRepo;

    private Booking booking;

    private Item item;

    private User owner;

    private User booker;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("test_owner");
        owner.setEmail("owner@test.ru");
        userService.create(owner);

        booker = new User();
        booker.setName("test_booker");
        booker.setEmail("booker@test.ru");
        userService.create(booker);

        item = new Item();
        item.setName("name");
        item.setDescription("test description");
        item.setAvailable(true);
        itemService.addNewItem(owner.getId(), item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setUserId(booker.getId());
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        service = new BookingServiceImpl(repository, userRepo, itemRepo);
    }

    @Test
    public void saveBooking() {
        service.addNewBooking(booker.getId(), booking);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.userId = :userId", Booking.class);
        Booking booking1 = query.setParameter("userId", booker.getId()).getSingleResult();

        assertThat(booking1.getId(), notNullValue());
        assertThat(booking1.getStart(), equalTo(booking.getStart()));
        assertThat(booking1.getEnd(), equalTo(booking.getEnd()));
        assertThat(booking1.getItemId(), equalTo(booking.getItemId()));
        assertThat(booking1.getUserId(), equalTo(booking.getUserId()));
    }

    @Test
    public void updateBooking() {
        service.addNewBooking(booker.getId(), booking);

        service.put(owner.getId(), booking.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(booking1.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void getBookingById() {
        service.addNewBooking(booker.getId(), booking);

        Booking b = service.getById(booker.getId(), booking.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(booking1.getId(), equalTo(b.getId()));
        assertThat(booking1.getStart(), equalTo(b.getStart()));
        assertThat(booking1.getEnd(), equalTo(b.getEnd()));
        assertThat(booking1.getItemId(), equalTo(b.getItemId()));
        assertThat(booking1.getUserId(), equalTo(b.getUserId()));
    }

    @Test
    public void getBookingsByUserId() {
        service.addNewBooking(booker.getId(), booking);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setUserId(owner.getId());
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.WAITING);

        List<Booking> bookings = service.getByUserId(booker.getId(), "ALL");

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.userId = :userId", Booking.class);
        List<Booking> bookings1 = query.setParameter("userId", booker.getId()).getResultList();

        assertThat(bookings.size(), equalTo(bookings1.size()));
    }

    @Test
    public void getBookingsByUserIdWithPagination() {
        service.addNewBooking(booker.getId(), booking);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setUserId(owner.getId());
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.WAITING);

        Page<Booking> bookings = service.getByUserIdWithPagination(booker.getId(), "ALL", 0, 2);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.userId = :userId", Booking.class);
        List<Booking> bookings1 = query.setParameter("userId", booker.getId()).getResultList();

        assertThat(((int) bookings.getTotalElements()), equalTo(bookings1.size()));
    }

    @Test
    public void getBookingsByOwnerId() {
        service.addNewBooking(booker.getId(), booking);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setUserId(owner.getId());
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.WAITING);

        List<Booking> bookings = service.getByOwnerId(owner.getId(), "ALL");

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b", Booking.class);
        List<Booking> bookings1 = query.getResultList();

        assertThat(bookings.size(), equalTo(bookings1.size()));
    }

    @Test
    public void getBookingsByOwnerIdWithPagination() {
        service.addNewBooking(booker.getId(), booking);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(20));
        booking.setItemId(item.getId());
        booking.setItem(item);
        booking.setUserId(owner.getId());
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.WAITING);

        Page<Booking> bookings = service.getByOwnerIdWithPagination(owner.getId(), "ALL", 0, 2);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b", Booking.class);
        List<Booking> bookings1 = query.getResultList();

        assertThat(((int) bookings.getTotalElements()), equalTo(bookings1.size()));
    }
}
