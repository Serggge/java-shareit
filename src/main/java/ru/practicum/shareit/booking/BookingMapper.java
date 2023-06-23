package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    public Booking mapToBooking(BookingDto bookingDto) {
        Booking entity = new Booking();
        entity.setStart(bookingDto.getStart());
        entity.setEnd(bookingDto.getEnd());
        return entity;
    }

    public BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setItemId(booking.getItem().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public List<BookingDto> mapToDto(Iterable<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(mapToDto(booking));
        }
        return result;
    }

}
