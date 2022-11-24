package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    @Getter
    private UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User put(int id, User user) {
        return userStorage.put(id, user);
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }
}
