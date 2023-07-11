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
class SimpleBookingDtoTest {

    @Autowired
    JacksonTester<SimpleBookingDto> json;

    @Test
    @SneakyThrows
    void testDto() {
        SimpleBookingDto simpleBookingDto = new SimpleBookingDto();
        Long bookingId = 1L;
        simpleBookingDto.setId(bookingId);
        Long bookerId = 2L;
        simpleBookingDto.setBookerId(bookerId);
        LocalDateTime dateTime = LocalDateTime.of(2023, 7, 6, 20, 20, 50);
        simpleBookingDto.setDateTime(dateTime);

        JsonContent<SimpleBookingDto> result = json.write(simpleBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingId.intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookerId.intValue());
        assertThat(result).extractingJsonPathValue("$.dateTime").isEqualTo(dateTime.toString());
    }
}