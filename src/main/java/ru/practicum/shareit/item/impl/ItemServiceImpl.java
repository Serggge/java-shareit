package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item add(Item item) {
        item = itemRepository.save(item);
        log.info("Добавлена новая вещь: {}", item);
        return item;
    }

    @Override
    public Item update(Item item) {
        checkItemOwner(item);
        item = updateFields(item);
        item = itemRepository.save(item);
        log.info("Изменено описание вещи: {}", item);
        return item;
    }

    @Override
    public Item getById(long itemId) {
        log.debug("Запрос вещи по id: " + itemId);
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }

    @Override
    public void deleteById(long itemId) {
        checkItemExistence(itemId);
        itemRepository.deleteById(itemId);
        log.info("Удалена вещь id=" + itemId);
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        userService.checkUserExistence(userId);
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> getByQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query);
    }

    private void checkItemExistence(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(String.format("Вещь с id=%d не найдена", itemId));
        }
    }

    private Item updateFields(Item item) {
        Item saved = getById(item.getId());
        String name = item.getName() == null ? saved.getName() : item.getName();
        String description = item.getDescription() == null ? saved.getDescription() : item.getDescription();
        boolean available = item.getAvailable() == null ? saved.getAvailable() : item.getAvailable();
        return Item.builder()
                .id(item.getId())
                .name(name)
                .description(description)
                .available(available)
                .owner(saved.getOwner())
                .build();
    }

    private void checkItemOwner(Item item) {
        Optional<Long> ownerId = itemRepository.findOwnerIdByItemId(item.getId());
        if (ownerId.isEmpty() || !ownerId.get().equals(item.getOwner().getId())) {
            throw new ItemNotFoundException(
                    String.format("Пользователь id=%d не является владельцем вещи id=%d",
                            item.getOwner().getId(), item.getId()));
        }
    }

}
