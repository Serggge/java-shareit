package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.*;

@JsonTest
class UserDtoTest {

    @Autowired
    JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void testUserDto() {
        UserDto dto = new UserDto();
        Long id = 1L;
        String name = "Name";
        String email = "email";
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(id.intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(email);
    }

}