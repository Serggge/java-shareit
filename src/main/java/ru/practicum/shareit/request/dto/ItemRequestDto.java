package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private Set<ItemDto> items = new HashSet<>();

}
