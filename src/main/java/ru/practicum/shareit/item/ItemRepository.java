package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query);

    List<Item> findAllByOwnerId(long id);

    @Query("select u.id " +
            "from Item as it " +
            "join it.owner as u " +
            "where u.id = ?1")
    Optional<Long> findOwnerIdByItemId(long itemId);

}
