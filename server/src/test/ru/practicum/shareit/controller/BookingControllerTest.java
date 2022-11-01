package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final User ownerUser = new User(1L, "Name", "test@test.ru");
    private final User someUser = new User(2L, "Name 2", "test2@test.ru");
    private final Item item = new Item(1L, "Клей", "Секундный клей момент", true, ownerUser,
            null);
    private final Booking booking = new Booking(1L, start, end, item, someUser, BookingStatus.WAITING);
    private final BookingDto bookingDto = BookingMapper.toBookingDto(booking);
    private final Booking bookingApproved = new Booking(2L, start, end, item, someUser, BookingStatus.APPROVED);
    private final BookingDto bookingApprovedDto = BookingMapper.toBookingDto(bookingApproved);
    private final Booking bookingRejected = new Booking(3L, start, end, item, someUser, BookingStatus.REJECTED);
    private final BookingDto bookingRejectedDto = BookingMapper.toBookingDto(bookingRejected);
    private final LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.format("%s", bookingDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.format("%s", bookingDto.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingApproved);
        mockMvc.perform(patch("/bookings/" + bookingApproved.getId() + "/?approved=true")
                        .header("X-Sharer-User-Id", bookingApproved.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingApproved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.format("%s", bookingApproved.getStart()))))
                .andExpect(jsonPath("$.end", is(String.format("%s", bookingApproved.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(bookingApproved.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingApproved.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingApproved.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingApproved.getStatus().toString())));
    }

    @Test
    void getByIdEndUserId() throws Exception {
        when(bookingService.getByIdEndUserId(anyLong(), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.format("%s", booking.getStart()))))
                .andExpect(jsonPath("$.end", is(String.format("%s", booking.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));

    }

    @Test
    void findAllByUserId() throws Exception {
        List<Booking> bookingList = List.of(booking, bookingApproved, bookingRejected);
        when(bookingService.findAllByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookingList);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(String.format("%s", bookingDto.getStart()))))
                .andExpect(jsonPath("$[0].end", is(String.format("%s", bookingDto.getEnd()))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingApprovedDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(String.format("%s", bookingApprovedDto.getStart()))))
                .andExpect(jsonPath("$[1].end", is(String.format("%s", bookingApprovedDto.getEnd()))))
                .andExpect(jsonPath("$[1].item.id", is(bookingApprovedDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookingApprovedDto.getItem().getName())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingApprovedDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingApprovedDto.getStatus().toString())))
                .andExpect(jsonPath("$[2].id", is(bookingRejectedDto.getId()), Long.class))
                .andExpect(jsonPath("$[2].start", is(String.format("%s", bookingRejectedDto.getStart()))))
                .andExpect(jsonPath("$[2].end", is(String.format("%s", bookingRejectedDto.getEnd()))))
                .andExpect(jsonPath("$[2].item.id", is(bookingRejectedDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[2].item.name", is(bookingRejectedDto.getItem().getName())))
                .andExpect(jsonPath("$[2].booker.id", is(bookingRejectedDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[2].status", is(bookingRejectedDto.getStatus().toString())));
    }

    @Test
    void findAllByOwnerId() throws Exception {
        List<Booking> bookingList = List.of(booking, bookingApproved, bookingRejected);
        when(bookingService.findAllByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookingList);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(String.format("%s", bookingDto.getStart()))))
                .andExpect(jsonPath("$[0].end", is(String.format("%s", bookingDto.getEnd()))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingApprovedDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(String.format("%s", bookingApprovedDto.getStart()))))
                .andExpect(jsonPath("$[1].end", is(String.format("%s", bookingApprovedDto.getEnd()))))
                .andExpect(jsonPath("$[1].item.id", is(bookingApprovedDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookingApprovedDto.getItem().getName())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingApprovedDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingApprovedDto.getStatus().toString())))
                .andExpect(jsonPath("$[2].id", is(bookingRejectedDto.getId()), Long.class))
                .andExpect(jsonPath("$[2].start", is(String.format("%s", bookingRejectedDto.getStart()))))
                .andExpect(jsonPath("$[2].end", is(String.format("%s", bookingRejectedDto.getEnd()))))
                .andExpect(jsonPath("$[2].item.id", is(bookingRejectedDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[2].item.name", is(bookingRejectedDto.getItem().getName())))
                .andExpect(jsonPath("$[2].booker.id", is(bookingRejectedDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[2].status", is(bookingRejectedDto.getStatus().toString())));
    }

}
