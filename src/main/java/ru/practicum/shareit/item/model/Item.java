package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.user.model.User;

@Value
@Builder
@Jacksonized
public class Item {

    long id;
    String name;
    String description;
    Boolean available;
    User owner;

    public Item withId(long id) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
    }

    public Item withOwner(User owner) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
    }

}
