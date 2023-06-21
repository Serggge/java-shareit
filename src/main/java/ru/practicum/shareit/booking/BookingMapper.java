package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class BookingMapper {

    public Booking mapToBooking(BookingDto bookingDto) {
        Booking entity = new Booking();
        LocalDate currentDay = bookingDto.getStart();
        while (currentDay.isBefore(bookingDto.getEnd()) || currentDay.isEqual(bookingDto.getEnd())) {
            entity.getBookingDays().add(currentDay);
            currentDay.plusDays(1);
        }
        return entity;
    }

    public BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setItemId(booking.getItem().getId());
        TreeSet<LocalDate> dates = new TreeSet<>(booking.getBookingDays());
        dto.setStart(dates.first());
        dto.setEnd(dates.last());
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
