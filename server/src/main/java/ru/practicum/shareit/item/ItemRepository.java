package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Item> findAllByQuery(String query, Pageable pageable);

    Page<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query("select u.id " +
            "from Item as it " +
            "join it.owner as u " +
            "where it.id = ?1")
    Optional<Long> findOwnerIdByItemId(long itemId);

    List<Item> findAllByItemRequestId(long itemRequestId);

    @Query("select it " +
            "from Item as it " +
            "join it.owner as u " +
            "join it.itemRequest as ir " +
            "where ir.id in ?1")
    List<Item> findAllByItemRequestId(Iterable<Long> ids);

}
