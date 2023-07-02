package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ItemOwnerDto extends ItemDto {

    private SimpleBookingDto lastBooking;
    private SimpleBookingDto nextBooking;
    private Set<CommentDto> comments = new HashSet<>();

}
