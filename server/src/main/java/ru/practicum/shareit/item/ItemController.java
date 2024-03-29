package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Validated
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto changeInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("id") long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto returnItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable("id") long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemOwnerDto> returnUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam Integer from,
                                              @RequestParam Integer size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> returnByQuery(@RequestParam("text") String query,
                                       @RequestParam Integer from,
                                       @RequestParam Integer size) {
        return itemService.getByQuery(query, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
