package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addNew(Booking booking);

    void approve(Booking booking);

    Booking getById(long bookingId);

    List<Booking> getUserBookings(long userId);

    List<Booking> getBookings(long userId);

}
