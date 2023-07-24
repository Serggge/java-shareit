package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void testDto() {
        BookingDto bookingDto = new BookingDto();
        Long bookingId = 1L;
        bookingDto.setId(bookingId);
        Long itemId = 2L;
        bookingDto.setItemId(itemId);
        LocalDateTime start = LocalDateTime.of(2023,7,6,20,30,50);
        bookingDto.setStart(start);
        LocalDateTime end = LocalDateTime.of(2023,8,7,10,30,50);
        bookingDto.setEnd(end);
        UserDto booker = new UserDto();
        Long bookerId = 3L;
        booker.setId(bookerId);
        String bookerName = "Booker Name";
        booker.setName(bookerName);
        String bookerEmail = "booker@email.com";
        booker.setEmail(bookerEmail);
        bookingDto.setBooker(booker);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        String itemName = "Item name";
        itemDto.setName(itemName);
        String itemDescription = "Item description";
        itemDto.setDescription(itemDescription);
        Long requestId = 3L;
        itemDto.setRequestId(requestId);
        itemDto.setAvailable(Boolean.TRUE);
        bookingDto.setItem(itemDto);
        Status status = Status.WAITING;
        bookingDto.setStatus(status);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingId.intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookerId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookerName);
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookerEmail);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(itemId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(requestId.intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
    }

}