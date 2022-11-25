package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User put(int id, User user);

    User findById(Integer id);

    void deleteUser(Integer id);
}
