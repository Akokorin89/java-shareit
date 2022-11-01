package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking create(long bookerId, Booking booking);

    Booking approveBooking(long bookerId, long bookingId, Boolean approved);

    Booking getByIdEndUserId(long bookingId, long userId);

    List<Booking> findAllByUserId(long userId, BookingState state, int from, int size);

    List<Booking> findAllByOwnerId(long ownerId, BookingState state, int from, int size);

    Booking findLastBookingByItemId(long itemId);

    Booking findNextBookingByItemId(long itemId);
}
