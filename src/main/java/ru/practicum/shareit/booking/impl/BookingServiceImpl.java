package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addNew(long userId, BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getStart() == null
                || bookingDto.getEnd() == null) {
            throw new RuntimeException("Некорректные даты бронирования");
        }
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        booking.setStatus(Status.WAITING);
        User user = userService.getById(userId);
        booking.setUser(user);
        Item item = itemService.getById(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new RuntimeException("вещь недоступна для бронирования");
        }
        booking.setItem(item);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public void approve(long userId, long bookingId, String approved) {
        User user = userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new UserNotFoundException("Вещь не пренадлежит пользователю");
        }
        try {
            boolean isApprove = Boolean.parseBoolean(approved);
            if (isApprove) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Недопустимый параметр approve");
        }
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
        if (booking.getUser().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.mapToDto(booking);
        } else {
         throw new UserNotFoundException("Пользователю запрещёно просматривать информацию по бронированию");
        }
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String state) {
        return null;
    }

    @Override
    public List<BookingDto> getItemsBookings(long userId, String state) {
        return null;
    }
}
