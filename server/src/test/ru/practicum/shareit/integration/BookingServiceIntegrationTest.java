package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void getAllBookings() {
        User user = userService.create(new User(null, "Name", "test@test.ru"));
        User someUser = userService.create(new User(null, "Name 2", "test2@test2.ru"));
        Item item = itemService.create(user.getId(), new Item(null, "Клей", "Секундный клей момент",
                true, user, null));
        Item item2 = itemService.create(user.getId(), new Item(null, "Клей 2",
                "Секундный клей момент", true, user, null));
        Item item3 = itemService.create(user.getId(), new Item(null, "Клей 3",
                "Секундный клей момент", true, user, null));
        Booking booking1 = bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.WAITING)
        );
        Booking booking2 = bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item2, someUser, BookingStatus.WAITING)
        );
        Booking booking3 = bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item3, someUser, BookingStatus.WAITING)
        );
        List<Booking> bookings = bookingService.findAllByOwnerId(user.getId(), BookingState.ALL, 0, 10);
        Assertions.assertEquals(3, bookings.size());
        List<Booking> bookingsFuture = bookingService.findAllByOwnerId(user.getId(), BookingState.FUTURE, 0, 10);
        Assertions.assertEquals(3, bookingsFuture.size());
        bookings = bookingService.findAllByUserId(someUser.getId(), BookingState.ALL, 0, 10);
        Assertions.assertEquals(3, bookings.size());
        bookings = bookingService.findAllByUserId(someUser.getId(), BookingState.FUTURE, 0, 10);
        Assertions.assertEquals(3, bookings.size());

        booking1.setStatus(BookingStatus.REJECTED);
        booking2.setStatus(BookingStatus.APPROVED);
        booking3.setStatus(BookingStatus.CANCELED);

        List<Booking> bookingRejected = bookingService.findAllByUserId(someUser.getId(), BookingState.REJECTED, 0, 10);
        Assertions.assertEquals(1, bookingRejected.size());
        List<Booking> bookingApproved = bookingService.findAllByUserId(someUser.getId(), BookingState.WAITING, 0, 10);
        Assertions.assertEquals(0, bookingApproved.size());
        List<Booking> bookingCurrent = bookingService.findAllByUserId(someUser.getId(), BookingState.CURRENT, 0, 10);
        Assertions.assertEquals(0, bookingCurrent.size());
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(8));
        List<Booking> bookingPast = bookingService.findAllByUserId(someUser.getId(), BookingState.PAST, 0, 10);
        Assertions.assertEquals(1, bookingPast.size());


        List<Booking> bookingRejectedOwner = bookingService.findAllByOwnerId(user.getId(), BookingState.REJECTED, 0, 10);
        Assertions.assertEquals(1, bookingRejectedOwner.size());
        List<Booking> bookingWaitingOwner = bookingService.findAllByOwnerId(user.getId(), BookingState.WAITING, 0, 10);
        Assertions.assertEquals(0, bookingWaitingOwner.size());
        List<Booking> bookingCurrentOwner = bookingService.findAllByOwnerId(user.getId(), BookingState.CURRENT, 0, 10);
        Assertions.assertEquals(0, bookingCurrentOwner.size());
        List<Booking> bookingPastOwner = bookingService.findAllByOwnerId(user.getId(), BookingState.PAST, 0, 10);
        Assertions.assertEquals(1, bookingPastOwner.size());

    }
}
