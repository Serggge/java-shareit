package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ItemOwnerDto extends ItemDto {

    private SimpleBookingDto lastBooking;
    private SimpleBookingDto nextBooking;
    private Set<CommentDto> comments = new HashSet<>();

}
