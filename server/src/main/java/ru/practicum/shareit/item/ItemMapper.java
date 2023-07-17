package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import java.util.Collection;
import java.util.List;

public interface ItemMapper {

    ItemDto toDto(Item item);

    List<ItemDto> toDto(Iterable<Item> items);

    ItemOwnerDto toOwnerDto(Item item, Collection<CommentDto> comments, Booking lastBooking, Booking nextBooking);

    Item toEntity(ItemDto itemDto);

}
