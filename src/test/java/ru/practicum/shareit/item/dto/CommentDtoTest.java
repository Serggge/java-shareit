package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void testDto() {
        CommentDto commentDto = new CommentDto();
        Long commentId = 1L;
        commentDto.setId(commentId);
        String commentText = "Some comment text";
        commentDto.setText(commentText);
        String authorName = "Author name";
        commentDto.setAuthorName(authorName);
        LocalDateTime created = LocalDateTime.of(2023,7,6,22,0,10);
        commentDto.setCreated(created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentId.intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentText);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(authorName);
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(created.toString());
    }
}