package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingMapper {

    Booking mapToBooking(BookingDto bookingDto);

    BookingDto mapToDto(Booking booking);

    List<BookingDto> mapToDto(Iterable<Booking> bookings);

}
