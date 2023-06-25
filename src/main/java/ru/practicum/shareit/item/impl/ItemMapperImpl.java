package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    @Override
    public List<ItemDto> toDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toDto(item));
        }
        return result;
    }

    @Override
    public ItemOwnerDto toOwnerDto(Item item, Collection<CommentDto> comments,
                                   Booking lastBooking, Booking nextBooking) {
        ItemOwnerDto dto = new ItemOwnerDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        SimpleBookingDto simpleLastBookingDto = null;
        if (lastBooking != null) {
            simpleLastBookingDto = new SimpleBookingDto(lastBooking.getId(), lastBooking.getBooker().getId(),
                    lastBooking.getEnd());
        }
        dto.setLastBooking(simpleLastBookingDto);
        SimpleBookingDto simpleNextBookingDto = null;
        if (nextBooking != null) {
            simpleNextBookingDto = new SimpleBookingDto(nextBooking.getId(), nextBooking.getBooker().getId(),
                    nextBooking.getStart());
        }
        dto.setNextBooking(simpleNextBookingDto);
        dto.getComments().addAll(comments);
        return dto;
    }

    @Override
    public Item toEntity(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

}
