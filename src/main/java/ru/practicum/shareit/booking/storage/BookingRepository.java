package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByIdDesc(Long id);

    List<Booking> findByItem_Owner_IdOrderByIdDesc(Long id);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStatus(Long bookerId, BookingStatus status);
}
