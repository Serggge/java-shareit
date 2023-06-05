package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Value
@Builder
public class ItemDto {

    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;

}
