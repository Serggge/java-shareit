package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingDto {

    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;

}
