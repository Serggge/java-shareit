package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ItemRequestMapperImpl implements ItemRequestMapper {

    private final ItemMapper itemMapper;

    @Override
    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    @Override
    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(mapItemToDto(itemRequest.getItems()));
        return dto;
    }

    @Override
    public List<ItemRequestDto> mapToDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(mapToDto(itemRequest));
        }
        return result;
    }

    private Set<ItemDto> mapItemToDto(Iterable<Item> items) {
        Set<ItemDto> result = new HashSet<>();
        for (Item item : items) {
            result.add(itemMapper.toDto(item));
        }
        return result;
    }

}
