package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(long userId, long itemRequestId);

    List<ItemRequestDto> getOwn(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

}
