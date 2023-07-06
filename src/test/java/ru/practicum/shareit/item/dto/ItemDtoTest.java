package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testDto() {
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

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(requestId.intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }

}