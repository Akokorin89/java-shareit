package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final User user = new User(1L, "Name", "test@test.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "Нужен Молоток", user,
            LocalDateTime.now().withNano(0));
    private final ItemRequest itemRequest2 = new ItemRequest(2L, "Нужна дрель", user,
            LocalDateTime.now().withNano(0));
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);
        when(userService.getById(anyLong()))
                .thenReturn(user);
        when(itemRequestService.create(any()))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(String.format("%s", itemRequestDto.getCreated()))));
    }

    @Test
    void findByOwnerItemRequest() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestMapper.toItemRequestDto(itemRequest, null),
                ItemRequestMapper.toItemRequestDto(itemRequest2, null));
        when(itemRequestService.findByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest, itemRequest2));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is(String.format("%s", itemRequestDtoList.get(0).getCreated()))))
                .andExpect(jsonPath("$[1].id", is(itemRequestDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description",
                        is(itemRequestDtoList.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created",
                        is(String.format("%s", itemRequestDtoList.get(1).getCreated()))));
    }

    @Test
    void findAllItemRequest() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestMapper.toItemRequestDto(itemRequest, null),
                ItemRequestMapper.toItemRequestDto(itemRequest2, null));
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest, itemRequest2));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created", is(String.format("%s",
                        itemRequestDtoList.get(0).getCreated()))))
                .andExpect(jsonPath("$[1].id", is(itemRequestDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description",
                        is(itemRequestDtoList.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created", is(String.format("%s",
                        itemRequestDtoList.get(1).getCreated()))));
    }

    @Test
    void getById() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);
        when(userService.getById(anyLong()))
                .thenReturn(user);
        when(itemRequestService.findById(anyLong()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/" + itemRequestDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(String.format("%s", itemRequestDto.getCreated()))));

    }
}
