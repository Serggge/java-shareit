package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where (lower(it.name) like %?1% or lower(it.description) like %?1%) " +
            "and it.available=true")
    List<Item> findAllByQuery(String query);

    List<Item> findAllByOwnerId(long id);

    @Query("select u.id " +
            "from Item as it " +
            "join it.owner as u " +
            "where it.id = ?1")
    Optional<Long> findOwnerIdByItemId(long itemId);

}
