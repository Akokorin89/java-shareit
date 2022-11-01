package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final User user = new User(1L, "Name", "test@test.ru");
    private final Item item = new Item(1L, "Клей", "Секундный клей момент", true, user, null);
    private final Item item2 = new Item(2L, "Клей 2", "Секундный клей момент", true, user, null);
    private final Item item3 = new Item(3L, "Клей 3", "Секундный клей момент", true, user, null);
    @MockBean
    ItemService itemService;
    @MockBean
    UserService userService;
    @MockBean
    BookingService bookingService;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void create() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.create(anyLong(), any()))
                .thenReturn(item);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void update() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/" + user.getId())
                        .header("X-Sharer-User-Id", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getById() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.getById(anyLong()))
                .thenReturn(item);
        mockMvc.perform(get("/items/" + user.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAllByUser() throws Exception {
        List<Item> itemList = List.of(item, item2, item3);
        List<ItemDto> itemDtoList = List.of(ItemMapper.toItemDto(item), ItemMapper.toItemDto(item2),
                ItemMapper.toItemDto(item3));
        when(itemService.getAllByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemList);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoList.get(0).getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoList.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoList.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoList.get(1).getAvailable())))
                .andExpect(jsonPath("$[2].id", is(itemDtoList.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(itemDtoList.get(2).getName())))
                .andExpect(jsonPath("$[2].description", is(itemDtoList.get(2).getDescription())))
                .andExpect(jsonPath("$[2].available", is(itemDtoList.get(2).getAvailable())));
    }

    @Test
    void searchByText() throws Exception {
        List<Item> itemList = List.of(item, item2, item3);
        List<ItemDto> itemDtoList = List.of(ItemMapper.toItemDto(item), ItemMapper.toItemDto(item2),
                ItemMapper.toItemDto(item3));
        when(itemService.searchByText(anyString(), anyInt(), anyInt()))
                .thenReturn(itemList);
        mockMvc.perform(get("/items/search?text=дрель")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoList.get(0).getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoList.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoList.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoList.get(1).getAvailable())))
                .andExpect(jsonPath("$[2].id", is(itemDtoList.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(itemDtoList.get(2).getName())))
                .andExpect(jsonPath("$[2].description", is(itemDtoList.get(2).getDescription())))
                .andExpect(jsonPath("$[2].available", is(itemDtoList.get(2).getAvailable())));
    }

    @Test
    void addCommentToItem() throws Exception {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        Comment comment = new Comment(1L, "Отзыв", item, user, created);
        when(userService.getById(anyLong()))
                .thenReturn(user);
        when(itemService.getById(anyLong()))
                .thenReturn(item);
        when(itemService.addCommentToItem(any()))
                .thenReturn(comment);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(String.format("%S", commentDto.getCreated()))));
    }
}
