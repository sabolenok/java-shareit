package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    List<User> findAllByIdIn(Collection<Integer> id);
}
