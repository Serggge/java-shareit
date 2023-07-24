package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create item request {}, userId={}", itemRequestDto, userId);
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> returnUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Get user item request, userId={}", userId);
        return itemRequestService.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> returnAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam Integer from,
                                          @RequestParam Integer size) {
        log.debug("Get all item requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto returnById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        log.debug("Get item request by id, itemRequestId={}, userId={}", requestId, userId);
        return itemRequestService.getById(userId, requestId);
    }

}
