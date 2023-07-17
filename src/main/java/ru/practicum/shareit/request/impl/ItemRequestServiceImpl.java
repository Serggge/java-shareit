package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private ItemRequestMapper itemRequestMapper;

    @Autowired
    public void setItemRequestMapper(ItemRequestMapper itemRequestMapper) {
        this.itemRequestMapper = itemRequestMapper;
    }

    @Override
    public ItemRequestDto add(long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setUser(userService.getById(userId));
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.mapToDto(itemRequest);
    }

    @Override
    public ItemRequestDto getById(long userId, long itemRequestId) {
        userService.checkUserExistence(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Не найден запрос с id=" + itemRequestId));
        itemRequest.addItems(itemRepository.findAllByItemRequestId(itemRequestId));
        return itemRequestMapper.mapToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwn(long userId) {
        userService.checkUserExistence(userId);
        return createListDto(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId));
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        int offset = from > 0 ? from / size : 0;
        Pageable page = PageRequest.of(offset, size, sortBy);
        return createListDto(itemRequestRepository.findByUserIdNot(userId, page).getContent());
    }

    private static ItemRequest createItemRequest(ItemRequest itemRequest, Collection<Item> items) {
        itemRequest.addItems(items);
        return itemRequest;
    }

    private List<ItemRequestDto> createListDto(Collection<ItemRequest> itemRequests) {
        Map<Long, ItemRequest> mapItemRequests = itemRequests.stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<Item>> mapItems = itemRepository.findAllByItemRequestId(mapItemRequests.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));
        return mapItemRequests.values()
                .stream()
                .map(itemRequest -> createItemRequest(itemRequest,
                        mapItems.getOrDefault(itemRequest.getId(), Collections.emptyList())))
                .map(itemRequestMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
