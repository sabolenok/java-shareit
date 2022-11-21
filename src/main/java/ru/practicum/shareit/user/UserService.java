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
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User put(User user) {
        return userStorage.put(user);
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }
}
