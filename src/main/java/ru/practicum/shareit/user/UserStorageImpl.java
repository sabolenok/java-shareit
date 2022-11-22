package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        checkEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User put(int id, User user) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        user.setId(id);
        User previous = users.get(id);
        user.setEmail(
                (user.getEmail() == null || user.getEmail().isBlank())
                        ? previous.getEmail()
                        : user.getEmail()
        );
        checkEmail(user);
        user.setName(
                (user.getName() == null || user.getName().isBlank())
                        ? previous.getName()
                        : user.getName()
        );
        users.put(user.getId(), user);
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

    private void checkEmail(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail()) && u.getId() != user.getId()) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }
    }
}
