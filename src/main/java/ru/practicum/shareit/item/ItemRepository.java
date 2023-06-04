package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(long itemId);

    List<Item> findAll();

    List<Item> findAllById(Collection<Long> ids);

    void deleteById(long itemId);

    boolean existsById(long itemId);

    List<Item> findByQuery(String query);

    List<Item> findItemsByUserId(long userId);

    boolean isSomeOwner(Item item);

}
