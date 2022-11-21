package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User put(User user);

    User findById(Integer id);
    void deleteUser(Integer id);
}
