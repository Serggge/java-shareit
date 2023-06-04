package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    public static long count;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        if (item.getId() == 0) {
            item = item.withId(++count);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAll() {
        return items.values()
                .stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllById(Collection<Long> ids) {
        return ids.stream()
                .map(items::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long itemId) {
        items.remove(itemId);
    }

    @Override
    public boolean existsById(long itemId) {
        return items.get(itemId) != null;
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSomeOwner(Item item) {
        return item.getOwner().getId() == items.get(item.getId()).getOwner().getId();
    }

    @Override
    public List<Item> findByQuery(String query) {
        String pattern = String.format("^.*%s.*$", query.toLowerCase());
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> Pattern.matches(pattern, item.getName().toLowerCase())
                        || Pattern.matches(pattern, item.getDescription().toLowerCase()))
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }

}
