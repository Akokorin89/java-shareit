package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidateExeption;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking create(Booking booking) {
        validation(booking);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Бронирование {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(long bookerId, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Бронирование не найдено"));
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new NoSuchElementException("Пользователь не найден"));
        if (approved == null)
            throw new NoSuchElementException("Одобрение пустое");
        if (!booking.getItem().getOwner().getId().equals(bookerId))
            throw new NoSuchElementException("Пользователь не владелец предмета");
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED))
            throw new ValidateExeption("Статус бронирования уже одобрен");
        if (!approved && booking.getStatus().equals(REJECTED))
            throw new ValidateExeption("Статус бронирования уже отклонен");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        log.info("Статус бронирования изменен {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getByIdEndUserId(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Бронирование не найдено"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("Пользователь не найден"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new NoSuchElementException("Бронирование не найдено");
        }
    }

    @Override
    public List<Booking> findAllByUserId(long userId, BookingState state) {
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByBooker_IdOrderByIdDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findByBooker_IdAndEndBefore(userId, now);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now);

                break;
            case FUTURE:
                bookingList = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        if (bookingList.isEmpty())
            throw new NoSuchElementException("Bookings not found");
        return bookingList;
    }

    @Override
    public List<Booking> findAllByOwnerId(long ownerId, BookingState state) {
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByItem_Owner_IdOrderByIdDesc(ownerId);
                break;
            case PAST:
                bookingList = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
        }
        if (bookingList.isEmpty())
            throw new NoSuchElementException("Bookings not found");
        return bookingList;
    }

    @Override
    public Booking findLastBookingByItemId(long itemId) {
        return bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
    }

    @Override
    public Booking findNextBookingByItemId(long itemId) {
        return bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
    }

    private void validation(Booking booking) {

        if (!booking.getItem().getAvailable())
            throw new ValidateExeption("Товар недоступен");

        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId()))
            throw new NoSuchElementException("Пользователь является владельцем предмета");

        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ValidateExeption("Start in past");
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new ValidateExeption("End before start");
        if (booking.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidateExeption("End in past");
    }
}
