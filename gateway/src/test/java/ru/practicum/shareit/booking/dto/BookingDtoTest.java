package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
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
        Long itemId = 2L;
        bookingDto.setItemId(itemId);
        LocalDateTime start = LocalDateTime.of(2023,7,6,20,30,50);
        bookingDto.setStart(start);
        LocalDateTime end = LocalDateTime.of(2023,8,7,10,30,50);
        bookingDto.setEnd(end);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(itemId.intValue());
    }

}
