package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistEmailException;

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
        checkEmail(user);
        return repository.save(user);
    }

    @Transactional
    public User put(int id, User user) {
        checkEmail(user);
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }

    private void checkEmail(User user) {
        User u = repository.findUserByEmail(user.getEmail());
        if (u.getEmail().equals(user.getEmail()) && u.getId() != user.getId()) {
                throw new AlreadyExistEmailException("Пользователь с таким email уже существует");
        }
    }
}
