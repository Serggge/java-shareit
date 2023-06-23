package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {

    BookingDto addNew(long userId, BookingDto bookingDto);

    void approve(long userId, long bookingId, String approved);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> getUserBookings(long userId, String state);

    List<BookingDto> getItemsBookings(long userId, String state);

}
