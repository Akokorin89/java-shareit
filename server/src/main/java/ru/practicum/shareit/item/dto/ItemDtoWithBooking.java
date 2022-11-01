package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;

    @Data
    public static class Booking {
        private Long id;
        private Long bookerId;
    }

    @Data
    public static class Comment {
        Long id;
        String text;
        String authorName;
        LocalDateTime created;
    }
}

