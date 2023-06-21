package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingController {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;


    @PostMapping
    public BookingDto create(@RequestHeader("X-Shared-User-Id") long userId,
                             @RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto);
        User user = userService.getById(userId);
        booking.setUser(user);
        Item item = itemService.getById(booking.getItem().getId());
        booking.setItem(item);

        return null;
    }
}
