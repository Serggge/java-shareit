package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

public interface ItemRequestMapper {

    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto mapToDto(ItemRequest itemRequest);

    List<ItemRequestDto> mapToDto(Iterable<ItemRequest> itemRequests);

}
