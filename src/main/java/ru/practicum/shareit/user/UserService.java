package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    @Getter
    private UserStorage userStorage;

    @Autowired
    @Getter
    private UserRepository repository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional
    public User create(User user) {
        return repository.save(user);
    }

    @Transactional
    public User put(int id, User user) {
        user.setId(id);
        Optional<User> previous = repository.findById(id);
        if (previous.isPresent()) {
            user.setEmail(
                    (user.getEmail() == null || user.getEmail().isBlank())
                            ? previous.get().getEmail()
                            : user.getEmail()
            );
            user.setName(
                    (user.getName() == null || user.getName().isBlank())
                            ? previous.get().getName()
                            : user.getName()
            );
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Transactional
    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }
}
