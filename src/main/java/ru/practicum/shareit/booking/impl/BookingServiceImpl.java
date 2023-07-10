package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingDateTimeException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.BookingNotAvailableException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserAccessException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private BookingMapper bookingMapper;

    @Autowired
    public void setBookingMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

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
                || bookingRepository.findBookingByDate(item.getId(), booking.getStart(), booking.getEnd())
                .isPresent()) {
            throw new BookingNotAvailableException("вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(booking.getBooker().getId())) {
            throw new BookingNotFoundException("Владелец не может бронировать свою вещь");
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
        boolean isApprove = Boolean.parseBoolean(approved);
        if (isApprove) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return bookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.mapToDto(booking);
        } else {
            throw new UserAccessException("Пользователю запрещёно просматривать информацию по бронированию");
        }
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String stateQuery, int from, int size) {
        userService.checkUserExistence(userId);
        State state;
        Page<Booking> result = Page.empty();
        try {
            state = State.valueOf(stateQuery.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + stateQuery);
        }
        LocalDateTime now = LocalDateTime.now();
        int offset = from > 0 ? from / size : 0;
        Pageable page = PageRequest.of(offset, size);
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, page);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, page);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        now, now, page);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
        }
        return bookingMapper.mapToDto(result);
    }

    @Override
    public List<BookingDto> getItemsBookings(long userId, String stateQuery, int from, int size) {
        userService.checkUserExistence(userId);
        State state;
        Page<Booking> result = Page.empty();
        int offset = from > 0 ? from / size : 0;
        Pageable page = PageRequest.of(offset, size);
        try {
            state = State.valueOf(stateQuery.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + stateQuery);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, page);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now, page);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
        }
        return bookingMapper.mapToDto(result);
    }

}
