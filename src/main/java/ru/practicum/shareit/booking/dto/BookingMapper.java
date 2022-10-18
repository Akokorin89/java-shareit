package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

    public static Booking toBooking(User booker, Item item, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}
