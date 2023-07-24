package ru.practicum.shareit.exception;

public class EmailExistingException extends RuntimeException {

    public EmailExistingException(String message) {
        super(message);
    }
}
