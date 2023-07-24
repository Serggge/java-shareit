package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BookingDto {

    private long id;
    @JsonProperty(access = WRITE_ONLY)
    @EqualsAndHashCode.Exclude
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private ItemDto item;
    private Status status;

}
