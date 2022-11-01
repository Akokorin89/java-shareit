package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    User user = new User(1L, "Name", "test@test.ru");
    ItemRequest itemRequest = new ItemRequest(1L, null, user, LocalDateTime.now());
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
    }

    @Test
    public void shouldValidateExceptionByIsBlankDescription() {
        Exception thrown = assertThrows(ValidateException.class, () -> itemRequestService.create(itemRequest));
        assertEquals("Description is empty", thrown.getMessage());
    }

    @Test
    void shouldNoSuchElementExceptionByNoSuchUserForGetRequest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(NoSuchElementException.class, () -> itemRequestService.findByRequesterId(1L));
        assertEquals("User not found", thrown.getMessage());
    }
}
