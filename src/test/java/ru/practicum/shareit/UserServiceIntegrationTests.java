package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
public class UserServiceIntegrationTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("test_user");
        user.setEmail("test@test.ru");

        service.setRepository(repository);
    }

    @Test
    public void saveUser() {
        service.create(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void updateUser() {
        service.create(user);

        user.setName("new_test_name");
        service.put(user.getId(), user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(user1.getId(), equalTo(user.getId()));
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));

        user.setEmail("newmail@test.ru");
        service.put(user.getId(), user);

        query = em.createQuery("Select u from User u where u.id = :id", User.class);
        user1 = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(user1.getId(), equalTo(user.getId()));
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void getUserById() {
        service.create(user);
        User userNew = new User();
        userNew.setName("new_user");
        userNew.setEmail("testmail@test.ru");
        service.create(userNew);

        User u = service.findById(user.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user1 = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(user1.getId(), equalTo(u.getId()));
        assertThat(user1.getName(), equalTo(u.getName()));
        assertThat(user1.getEmail(), equalTo(u.getEmail()));
    }

    @Test
    public void getAllUsers() {
        service.create(user);
        User userNew = new User();
        userNew.setName("new_user");
        userNew.setEmail("testmail@test.ru");
        service.create(userNew);

        List<User> u = service.findAll();

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(2));
        assertThat(users.size(), equalTo(u.size()));
    }

    @Test
    public void deleteFirstUser() {
        service.create(user);
        User userNew = new User();
        userNew.setName("new_user");
        userNew.setEmail("testmail@test.ru");
        service.create(userNew);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(2));

        service.deleteUser(user.getId());

        query = em.createQuery("Select u from User u", User.class);
        users = query.getResultList();

        assertThat(users.size(), equalTo(1));
    }
}
