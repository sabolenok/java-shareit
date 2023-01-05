package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByUserIdOrderById(Integer userId);

    Page<Item> findAllByUserIdOrderById(Integer userId, Pageable pageable);

    List<Item> findByNameLikeIgnoreCaseAndAvailableOrderById(String text, Boolean available);

    List<Item> findByDescriptionLikeIgnoreCaseAndAvailableOrderById(String text, Boolean available);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM items i WHERE upper(name) ILIKE '%' || ? || '%' and is_available " +
                    "UNION SELECT * FROM items i WHERE upper(description) ILIKE '%' || ? || '%' and is_available " +
                    "ORDER BY id"
    )
    Page<Item> findByNameOrDescriptionNative(String text, String text1, Pageable pageable);

    Optional<Item> findByIdAndUserId(int id, int userId);
}
