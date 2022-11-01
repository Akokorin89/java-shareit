package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("Add ItemRequest userId={}, itemRequestDto={}", userId, itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByOwnerItemRequest(@RequestHeader(USER_ID_HEADER) long requesterId) {
        log.info("Get ItemRequest requesterId={}", requesterId);
        return itemRequestClient.findByOwnerItemRequest(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequest(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
            @Positive @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Get all ItemRequests userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long requestId) {
        log.info("Get ItemRequest requestId={}, userId={}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }

}
