package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestBody BookingDto bookingDto,
            @RequestHeader(USER_ID_HEADER) long bookerId
    ) {
        log.info("Creating booking={}, userId={}", bookingDto, bookerId);
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(bookingService.create(bookerId, booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader(USER_ID_HEADER) long bookerId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("Booking approved userId={}, bookingId={}, approved={}", bookerId, bookingId, approved);
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getByIdEndUserId(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get booking={}, userId={}", bookingId, userId);
        return BookingMapper.toBookingDto(bookingService.getByIdEndUserId(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findAllByUserId(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingState state,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get bookings with state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.findAllByUserId(userId, state, from, size).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL", required = false) BookingState state,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get bookings owner with state={}, userId={}, from={}, size={}", state, ownerId, from, size);
        return bookingService.findAllByOwnerId(ownerId, state, from, size).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
