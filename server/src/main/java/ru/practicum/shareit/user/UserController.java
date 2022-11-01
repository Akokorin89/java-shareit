package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get users");
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Get user id={}", id);
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Add user userDto={}", userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(
                userService.create(user)
        );
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Update user userDto={}", userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        log.info("Remove user id={}", id);
        userService.remove(id);
    }
}
