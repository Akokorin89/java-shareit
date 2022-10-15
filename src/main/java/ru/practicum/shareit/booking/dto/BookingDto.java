package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private long itemId;
    private User booker;
    private BookingStatus status;

    @Data
    public static class Item {
        private Long id;
        private String name;
    }

    @Data
    public static class User {
        private Long id;
    }
}
