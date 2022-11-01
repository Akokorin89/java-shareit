package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking create(long bookerId, Booking booking) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        validation(booking);
        log.info("Create {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(long bookerId, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Booking not found"));
        userRepository.findById(bookerId).orElseThrow(
                () -> new NoSuchElementException("User not found"));
        if (approved == null)
            throw new NoSuchElementException("approved is null");
        if (!booking.getItem().getOwner().getId().equals(bookerId))
            throw new NoSuchElementException("User not owner item");
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED))
            throw new ValidateException("Booking status is already approved");
        if (!approved && booking.getStatus().equals(BookingStatus.REJECTED))
            throw new ValidateException("Booking status is already rejected");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info("Change status {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getByIdEndUserId(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Booking not found"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("User not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new NoSuchElementException("Booking not found");
        }
    }

    @Override
    public List<Booking> findAllByUserId(long userId, BookingState state, int from, int size) {
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByBooker_IdOrderByIdDesc(userId, pageable);
                log.info("Get all bookings userId={}, bookingList={}", userId, bookingList);
                break;
            case PAST:
                bookingList = bookingRepository.findByBooker_IdAndEndBefore(userId, now, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return bookingList;
    }

    @Override
    public List<Booking> findAllByOwnerId(long ownerId, BookingState state, int from, int size) {
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByItem_Owner_IdOrderByIdDesc(ownerId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now,
                        pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now,
                        pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED,
                        pageable);
                break;
        }
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
        //Check available
        if (!booking.getItem().getAvailable())
            throw new ValidateException("Item is not available");
        //Check owner
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId()))
            throw new NoSuchElementException("User is owner item");
        //Check start and end
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ValidateException("Start in past");
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new ValidateException("End before start");
        if (booking.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidateException("End in past");
    }
}
