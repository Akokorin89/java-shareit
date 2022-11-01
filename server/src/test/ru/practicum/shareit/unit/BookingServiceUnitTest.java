package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    private final User ownerUser = new User(1L, "Name", "test@test.ru");
    private final User someUser = new User(2L, "Name 2", "test2@test.ru");
    private final Item item = new Item(1L, "Клей", "Секундный клей момент", true, ownerUser,
            null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            item, someUser, BookingStatus.WAITING);
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    BookingService bookingService;

    @BeforeEach
    public void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void shouldValidateExceptionItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception thrown = assertThrows(ValidateException.class, () -> bookingService.create(1L, booking));
        assertEquals("Item is not available", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionBookerIsOwnerItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(ownerUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setBooker(ownerUser);
        Exception thrown = assertThrows(NoSuchElementException.class, () -> bookingService.create(1L, booking));
        assertEquals("User is owner item", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionStartInPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setStart(LocalDateTime.now().minusDays(1));
        Exception thrown = assertThrows(ValidateException.class, () -> bookingService.create(1L, booking));
        assertEquals("Start in past", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionEndBeforeStart() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        Exception thrown = assertThrows(ValidateException.class, () -> bookingService.create(1L, booking));
        assertEquals("End before start", thrown.getMessage());
    }

    @Test
    public void shouldExceptionBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Booking not found", thrown.getMessage());
    }

    @Test
    public void shouldExceptionGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.getByIdEndUserId(1, 2));
        assertEquals("Booking not found", thrown.getMessage());

    }

    @Test
    public void shouldExceptionUserNotFoundGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.getByIdEndUserId(1, 2));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void shouldExceptionUserNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void shouldExceptionApprovedIsNull() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(ownerUser));
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.approveBooking(1, 2, null));
        assertEquals("approved is null", thrown.getMessage());
    }

    @Test
    public void shouldExceptionUserNotOwnerItem() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.approveBooking(2, 2, true));
        assertEquals("User not owner item", thrown.getMessage());
    }

    @Test
    public void shouldExceptionBookingStatusIsApprovedTrue() {
        Booking bookingApproved = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingApproved));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(ValidateException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Booking status is already approved", thrown.getMessage());
        bookingApproved.setStatus(BookingStatus.APPROVED);
        Exception thrownExcept = assertThrows(ValidateException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Booking status is already approved", thrownExcept.getMessage());
    }

    @Test
    public void shouldExceptionBookingStatusIsApprovedFalse() {
        Booking bookingRejected = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingRejected));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(ValidateException.class, () ->
                bookingService.approveBooking(1, 2, false));
        assertEquals("Booking status is already rejected", thrown.getMessage());
        bookingRejected.setStatus(BookingStatus.WAITING);
        bookingRejected.setStatus(BookingStatus.REJECTED);
        Exception thrownExcept = assertThrows(ValidateException.class, () ->
                bookingService.approveBooking(1, 2, false));
        assertEquals("Booking status is already rejected", thrownExcept.getMessage());
    }

    @Test
    public void shouldExceptionСreate() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.create(1, booking));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void shouldExceptionCreateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.create(1, booking));
        assertEquals("Item not found", thrown.getMessage());
    }

    @Test
    public void shouldExceptionGetById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () ->
                bookingService.getByIdEndUserId(1, 1));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void shouldGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Booking bookingTest = bookingService.getByIdEndUserId(1, 2);
        assertEquals(booking, bookingTest);
        assertEquals(2, booking.getBooker().getId());
        assertEquals(1, booking.getItem().getOwner().getId());
    }

}
