package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Long itemRequestId = 1L;
        itemRequestDto.setId(itemRequestId);
        String description = "Item request description";
        itemRequestDto.setDescription(description);
        LocalDateTime created = LocalDateTime.of(2023, 7, 6, 20, 0,1);
        itemRequestDto.setCreated(created);
        System.out.println(created);
        ItemDto itemDto = new ItemDto();
        Long itemId = 2L;
        itemDto.setId(itemId);
        String itemName = "Item name";
        itemDto.setName(itemName);
        String itemDescription = "Item description";
        itemDto.setDescription(itemDescription);
        Long requestId = 3L;
        itemDto.setRequestId(requestId);
        itemDto.setAvailable(Boolean.TRUE);
        itemRequestDto.getItems().add(itemDto);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathArrayValue("$..items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(itemId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(requestId.intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
    }

}
