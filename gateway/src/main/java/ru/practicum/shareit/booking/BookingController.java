package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID_HEADER) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("Booking approved userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
            @Positive @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get Bookings owner userId={}, state={}, from={}, size={}", userId, stateParam, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }
}
