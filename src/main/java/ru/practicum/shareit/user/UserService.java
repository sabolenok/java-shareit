package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    @Autowired
    @Getter
    private UserStorage userStorage;

    public Collection<User> findAll() {
        log.info("Получен запрос к эндпоинту GET /users");
        return userStorage.findAll();
    }

    public User create(User user) {
        log.info("Получен запрос к эндпоинту POST /users");
        return userStorage.create(user);
    }

    public User put(User user) {
        log.info("Получен запрос к эндпоинту PUT /users");
        return userStorage.put(user);
    }

    public User findById(Integer id) {
        log.info("Получен запрос к эндпоинту GET /users/{id}");
        return userStorage.findById(id);
    }
}
