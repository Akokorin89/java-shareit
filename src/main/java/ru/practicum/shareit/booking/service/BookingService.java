package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking);

    Booking approveBooking(long bookerId, long bookingId, Boolean approved);

    Booking getByIdEndUserId(long bookingId, long userId);

    List<Booking> findAllByUserId(long userId, BookingState state);

    List<Booking> findAllByOwnerId(long ownerId, BookingState state);

    Booking findLastBookingByItemId(long itemId);

    Booking findNextBookingByItemId(long itemId);

}
