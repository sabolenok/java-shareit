package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceIntegrationTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @Test
    public void saveUser() {
        service.setRepository(repository);

        User user = new User();
        user.setName("test_user");
        user.setEmail("test@test.ru");

        service.create(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }
}
