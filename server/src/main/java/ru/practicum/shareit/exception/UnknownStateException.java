package ru.practicum.shareit.exception;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String s) {
        super(String.format("Unknown state: %s", s));
    }
}
