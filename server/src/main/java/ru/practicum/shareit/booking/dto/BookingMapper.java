package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.Item item = new BookingDto.Item();
        BookingDto.User booker = new BookingDto.User();
        if (booking.getItem() != null) {
            item.setId(booking.getItem().getId());
            item.setName(booking.getItem().getName());
        }
        if (booking.getBooker() != null)
            booker.setId(booking.getBooker().getId());
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Item item = new Item();
        if (bookingDto.getItemId() != null)
            item.setId(bookingDto.getItemId());
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart().withNano(0));
        booking.setEnd(bookingDto.getEnd().withNano(0));
        booking.setItem(item);
        return booking;
    }
}
