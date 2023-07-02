package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingMapperImpl implements BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public Booking mapToBooking(BookingDto bookingDto) {
        Booking entity = new Booking();
        entity.setStart(bookingDto.getStart());
        entity.setEnd(bookingDto.getEnd());
        return entity;
    }

    @Override
    public BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        if (booking.getBooker() != null) {
            dto.setBooker(userMapper.toDto(booking.getBooker()));
        }
        if (booking.getItem() != null) {
            dto.setItem(itemMapper.toDto(booking.getItem()));
        }
        dto.setStatus(booking.getStatus());
        return dto;
    }

    @Override
    public List<BookingDto> mapToDto(Iterable<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(mapToDto(booking));
        }
        return result;
    }

}
