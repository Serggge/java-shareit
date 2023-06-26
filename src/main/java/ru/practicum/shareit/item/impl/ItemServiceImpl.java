package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto add(long userId, ItemDto dto) {
        Item item = itemMapper.toEntity(dto);
        User user = userService.getById(userId);
        item = item.withOwner(user);
        item = itemRepository.save(item);
        log.info("Добавлена новая вещь: {}", item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto dto) {
        User user = userService.getById(userId);
        Item item = itemMapper.toEntity(dto);
        item = item.withId(itemId).withOwner(user);
        checkItemOwner(item);
        item = updateFields(item);
        item = itemRepository.save(item);
        log.info("Изменено описание вещи: {}", item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        log.debug("Запрос вещи по id: " + itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        List<CommentDto> commentDtos = commentMapper.mapToDto(commentRepository.findByItemId(itemId));
        List<Booking> bookings;
        if (item.getOwner().getId() != userId) {
            return itemMapper.toOwnerDto(item, commentDtos,null, null);
        } else {
            bookings = bookingRepository.findAllByItemId(itemId);
            return itemMapper.toOwnerDto(item, commentDtos,
                    defineLastBooking(bookings), defineNextBooking(bookings));
        }

    }

    @Override
    public void deleteById(long itemId) {
        checkItemExistence(itemId);
        itemRepository.deleteById(itemId);
        log.info("Удалена вещь id=" + itemId);
    }

    @Override
    public List<ItemOwnerDto> getItemsByUserId(long userId) {
        userService.checkUserExistence(userId);
        Map<Long, Item> mapItems = itemRepository.findAllByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Booking>> mapBookings = bookingRepository.findAllByItemIdAndOwnerId(mapItems.keySet())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> mapComments = commentRepository.findAllByItemId(mapItems.keySet())
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        return mapItems.values()
                .stream()
                .map(item -> itemMapper.toOwnerDto(item,
                        commentMapper.mapToDto(mapComments.getOrDefault(item.getId(), new ArrayList<>())),
                        defineLastBooking(mapBookings.get(item.getId())),
                        defineNextBooking(mapBookings.get(item.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findAllByQuery(query.toLowerCase());
        return itemMapper.toDto(items);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = userService.getById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        if (bookingRepository.findSuccessfulUserBooking(itemId, userId).isEmpty()) {
            throw new BookingNotAvailableException(
                    String.format("Пользователь id=%d не пользовался вещью id=%d", userId, itemId));
        }
        Comment comment = commentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.mapToDto(comment);
    }

    private void checkItemExistence(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(String.format("Вещь с id=%d не найдена", itemId));
        }
    }

    private Item updateFields(Item item) {
        Item saved = itemRepository.findById(item.getId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id=%d не найдена", item.getId())));
        String name;
        if (item.getName() == null || item.getName().isBlank()) {
            name = saved.getName();
        } else {
            name = item.getName();
        }
        String description;
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            description = saved.getDescription();
        } else {
            description = item.getDescription();
        }
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

    private Booking defineLastBooking(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        } else {
            LocalDateTime now = LocalDateTime.now();
            return bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }
    }

    private Booking defineNextBooking(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        } else {
            LocalDateTime now = LocalDateTime.now();
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }
    }

}
