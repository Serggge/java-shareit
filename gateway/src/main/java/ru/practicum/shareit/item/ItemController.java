package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.debug("Create Item {}, by User with ID={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> changeInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("id") long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Update info for Item with ID={}, change Data={}, by User with ID={}", itemId, itemDto, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> returnItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable("id") long itemId) {
        log.debug("Get Item by ID={}, by User with ID={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> returnUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Get item with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> returnByQuery(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam String text,
                                                @PositiveOrZero
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Get item by query={}, with userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.getItemsByQuery(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Create comment {}, itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
