package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addNew(long userId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingDateTimeException("Некорректные даты бронирования");
        }
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        User user = userService.getById(userId);
        booking.setBooker(user);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id=%d не найдена", bookingDto.getItemId())));
        if (!item.getAvailable()
                || bookingRepository.hasBooking(item.getId(), booking.getStart(), booking.getEnd())
                .isPresent()) {
            throw new BookingNotAvailableException("вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(booking.getBooker().getId())) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(long userId, long bookingId, String approved) {
        User user = userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new UserNotFoundException("Вещь не пренадлежит пользователю");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new IllegalArgumentException("Статус был подтверждён владельцем ранее");
        }
        try {
            boolean isApprove = Boolean.parseBoolean(approved);
            if (isApprove) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            booking = bookingRepository.save(booking);
        } catch (Exception e) {
            throw new IllegalArgumentException("Недопустимый параметр approve");
        }
        return bookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.mapToDto(booking);
        } else {
            throw new UserNotFoundException("Пользователю запрещёно просматривать информацию по бронированию");
        }
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String stateQuery) {
        userService.checkUserExistence(userId);
        State state;
        List<Booking> result = new ArrayList<>();
        try {
            state = State.valueOf(stateQuery.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + stateQuery);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
        }
        return bookingMapper.mapToDto(result);
    }

    @Override
    public List<BookingDto> getItemsBookings(long userId, String stateQuery) {
        userService.checkUserExistence(userId);
        State state;
        List<Booking> result = new ArrayList<>();
        try {
            state = State.valueOf(stateQuery.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + stateQuery);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
        }
        return bookingMapper.mapToDto(result);
    }
}
