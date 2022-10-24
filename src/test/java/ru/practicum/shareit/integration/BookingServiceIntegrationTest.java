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
        bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.WAITING)
        );
        bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item2, someUser, BookingStatus.WAITING)
        );
        bookingService.create(someUser.getId(), new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item3, someUser, BookingStatus.WAITING)
        );
        List<Booking> bookings = bookingService.findAllByOwnerId(user.getId(), BookingState.ALL, 0, 10);
        Assertions.assertEquals(3, bookings.size());
        bookings = bookingService.findAllByUserId(someUser.getId(), BookingState.ALL, 0, 10);
        Assertions.assertEquals(3, bookings.size());
    }
}
