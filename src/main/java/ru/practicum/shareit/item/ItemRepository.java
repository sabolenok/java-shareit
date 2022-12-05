package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByUserId(Integer userId);

    List<Item> findByNameLikeIgnoreCaseAndAvailableOrderById(String text, Boolean available);

    List<Item> findByDescriptionLikeIgnoreCaseAndAvailableOrderById(String text, Boolean available);

    Optional<Item> findByIdAndUserId(int id, int userId);
}
