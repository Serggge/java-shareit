package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import ru.practicum.shareit.config.serialize.LocalDateTimeSerializer;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final String message;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        timestamp = LocalDateTime.now();
    }
}
