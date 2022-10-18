package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemDtoWithBooking {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
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
