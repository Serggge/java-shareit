package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingController {

    private final BookingService bookingService;
    private final ItemService itemService;


    @PostMapping
    public BookingDto create(@RequestHeader("X-Shared-User-Id") long userId,
                             @RequestBody BookingDto bookingDto) {

        return null;
    }
}
