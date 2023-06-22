package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDto addNew(long userId, BookingDto bookingDto);

    void approve(long userId, long bookingId, String approved);

    BookingDto getById(long bookingId);

    List<BookingDto> getUserBookings(long userId);

    List<BookingDto> getBookings(long userId);

}
