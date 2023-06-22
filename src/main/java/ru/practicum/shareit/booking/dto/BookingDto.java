package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingDto {

    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}
