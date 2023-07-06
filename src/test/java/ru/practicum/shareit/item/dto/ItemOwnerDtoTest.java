package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOwnerDtoTest {

    @Autowired
    JacksonTester<ItemOwnerDto> json;

    @Test
    @SneakyThrows
    void testDto() {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        Long itemId = 2L;
        itemOwnerDto.setId(itemId);
        String itemName = "Item name";
        itemOwnerDto.setName(itemName);
        String itemDescription = "Item description";
        itemOwnerDto.setDescription(itemDescription);
        Long requestId = 3L;
        itemOwnerDto.setRequestId(requestId);
        itemOwnerDto.setAvailable(Boolean.TRUE);
        SimpleBookingDto nextBooking = new SimpleBookingDto();
        Long nextBookingId = 4L;
        nextBooking.setId(nextBookingId);
        Long nextBookingBookerId = 5L;
        nextBooking.setBookerId(nextBookingBookerId);
        LocalDateTime nextBookingDateTime = LocalDateTime.of(2023, 8, 8, 10, 20, 30);
        nextBooking.setDateTime(nextBookingDateTime);
        itemOwnerDto.setNextBooking(nextBooking);
        SimpleBookingDto lastBooking = new SimpleBookingDto();
        Long lastBookingId = 6L;
        lastBooking.setId(lastBookingId);
        Long lastBookingBookerId = 7L;
        lastBooking.setBookerId(lastBookingBookerId);
        LocalDateTime lastBookingDateTime = LocalDateTime.of(2023, 5, 4, 14, 10, 20);
        lastBooking.setDateTime(lastBookingDateTime);
        itemOwnerDto.setLastBooking(lastBooking);
        CommentDto commentDto = new CommentDto();
        Long commentId = 1L;
        commentDto.setId(commentId);
        String commentText = "Some comment text";
        commentDto.setText(commentText);
        String authorName = "Author name";
        commentDto.setAuthorName(authorName);
        LocalDateTime created = LocalDateTime.of(2023,7,6,22,0,10);
        commentDto.setCreated(created);
        itemOwnerDto.getComments().add(commentDto);

        JsonContent<ItemOwnerDto> result = json.write(itemOwnerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(requestId.intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(lastBookingId.intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(lastBookingBookerId.intValue());
        assertThat(result).extractingJsonPathValue("$.lastBooking.dateTime")
                .isEqualTo(lastBookingDateTime.toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBookingId.intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(nextBookingBookerId.intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking.dateTime")
                .isEqualTo(nextBookingDateTime.toString());
        assertThat(result).extractingJsonPathArrayValue("$..comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(commentId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(commentText);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(authorName);
        assertThat(result).extractingJsonPathValue("$.comments[0].created").isEqualTo(created.toString());
    }

}