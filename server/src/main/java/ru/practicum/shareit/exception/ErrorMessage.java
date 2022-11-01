package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorMessage {
    private final String error;
}
