package ru.practicum.shareit.exception;

public class BookingNotAvailableException extends RuntimeException {

    public BookingNotAvailableException(String message) {
        super(message);
    }
}
