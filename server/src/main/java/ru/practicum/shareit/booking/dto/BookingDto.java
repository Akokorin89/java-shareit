package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long itemId;
    private User booker;
    private BookingStatus status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }

    @Data
    public static class User {
        private Long id;
    }
}
