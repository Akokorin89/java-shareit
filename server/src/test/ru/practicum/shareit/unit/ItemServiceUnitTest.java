package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
    private final User user = new User(1L, "Name", "test@test.ru");
    private final User someUser = new User(2L, "Name 2", "test2@test2.ru");
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    Item item = new Item(1L, "Клей", "Секундный клей момент", true, user, null);
    private final Comment comment = new Comment(1L, "Коммнтарий", item, user, LocalDateTime.now());
    private ItemService itemService;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    public void shouldItemNotFoundByWrongItemId() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () -> itemService.update(user.getId(), 1L,
                item));
        assertEquals("Item not found", thrown.getMessage());
    }

    @Test
    public void shouldAccessDeniedByUserNotOwner() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(NoSuchElementException.class, () -> itemService.update(someUser.getId(),
                1L, item));
        assertEquals("Access denied", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionIsUserNotBookingItem() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(someUser));
        Mockito
                .when(bookingRepository.findByBooker_IdAndEndBefore(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of());
        Exception thrown = assertThrows(ValidateException.class, () -> itemService.addCommentToItem(comment));
        assertEquals("User not booking its item", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionItemCreate() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception thrown = assertThrows(NoSuchElementException.class, () -> itemService.create(1, item));
        assertEquals("User not found", thrown.getMessage());
    }

}
