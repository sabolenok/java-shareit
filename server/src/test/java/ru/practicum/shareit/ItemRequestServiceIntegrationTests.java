package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceIntegrationTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRequestServiceImpl service;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    private ItemRequest itemRequest;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("test_user");
        user.setEmail("test@test.ru");
        userService.create(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("test request description");
        itemRequest.setRequestorId(user.getId());

        service = new ItemRequestServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    public void saveItemRequest() {
        service.addNewItemRequest(user.getId(), itemRequest);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select ir from ItemRequest ir where ir.requestorId = :userId",
                ItemRequest.class);
        ItemRequest itemRequest1 = query.setParameter("userId", user.getId()).getSingleResult();

        assertThat(itemRequest1.getId(), notNullValue());
        assertThat(itemRequest1.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequest1.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequest1.getRequestorId(), equalTo(itemRequest.getRequestorId()));
    }

    @Test
    public void getItemRequestById() {
        service.addNewItemRequest(user.getId(), itemRequest);

        ItemRequest ir = service.getById(user.getId(), itemRequest.getId());

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select ir from ItemRequest ir where ir.requestorId = :userId",
                ItemRequest.class);
        ItemRequest itemRequest1 = query.setParameter("userId", user.getId()).getSingleResult();

        assertThat(itemRequest1.getId(), equalTo(ir.getId()));
        assertThat(itemRequest1.getDescription(), equalTo(ir.getDescription()));
        assertThat(itemRequest1.getCreated(), equalTo(ir.getCreated()));
        assertThat(itemRequest1.getRequestorId(), equalTo(ir.getRequestorId()));
    }

    @Test
    public void getAllItemRequests() {
        service.addNewItemRequest(user.getId(), itemRequest);

        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setDescription("description of a second test item request");
        newItemRequest.setRequestorId(user.getId());
        service.addNewItemRequest(user.getId(), newItemRequest);

        List<ItemRequest> ir = service.getAllForUser(user.getId());

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select ir from ItemRequest ir",
                ItemRequest.class);
        List<ItemRequest> itemRequests = query.getResultList();

        assertThat(itemRequests.size(), equalTo(2));
        assertThat(itemRequests.size(), equalTo(ir.size()));
    }

    @Test
    public void getAllItemRequestsWithPagination() {
        service.addNewItemRequest(user.getId(), itemRequest);

        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setDescription("description of a second test item request");
        newItemRequest.setRequestorId(user.getId());
        service.addNewItemRequest(user.getId(), newItemRequest);

        Page<ItemRequest> ir = service.getAllOthersUsers(user.getId(), 0, 2);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select ir from ItemRequest ir",
                ItemRequest.class);
        List<ItemRequest> itemRequests = query.getResultList();

        assertThat(itemRequests.size(), equalTo(2));
        assertThat(itemRequests.size(), equalTo(ir.getSize()));
    }
}
