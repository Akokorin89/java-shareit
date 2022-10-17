package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constant.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingDto create(
            @Valid @RequestBody BookingDto bookingDto,
            @RequestHeader(USER_ID_HEADER) long bookerId
    ) {
        User booker = userService.getById(bookerId);
        Item item = itemService.getById(bookingDto.getItemId());
        Booking booking = BookingMapper.toBooking(booker, item, bookingDto);
        return BookingMapper.toBookingDto(bookingService.create(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader(USER_ID_HEADER) long bookerId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getByIdEndUserId(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.getByIdEndUserId(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findAllByUserId(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state

    ) {
        BookingState stater;
        try {
            stater = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state:" + state);
        }
        //Типа валидация:)
        User owner = userService.getById(userId);
        return bookingService.findAllByUserId(userId, stater).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL", required = false) String state
            //
    ) {
        BookingState stater;
        try {
            stater = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state:" + state);
        }
        User owner = userService.getById(ownerId);
        return bookingService.findAllByOwnerId(ownerId, stater).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
