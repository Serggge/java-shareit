package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create item request={}, userId={}", itemRequestDto, userId);
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> returnUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Get request by userId={}", userId);
        return itemRequestClient.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> returnAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PositiveOrZero
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Get all item requests, with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> returnById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        log.debug("Get request by id={}, userId={}", requestId, userId);
        return itemRequestClient.getById(userId, requestId);
    }

}
