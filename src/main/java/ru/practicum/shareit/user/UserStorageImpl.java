package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    @Getter
    private static Integer id = 0;

    private static Integer getNextId() {
        return ++id;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь \"'{}'\" создан", user.getName());

        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь \"'{}'\" изменен", user.getName());

        return user;
    }

    @Override
    public User findById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(Integer id) {
        findById(id);
        users.remove(id);
    }
}
