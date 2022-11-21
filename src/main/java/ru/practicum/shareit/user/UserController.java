package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    public final UserService userService;
    @Autowired
    private final UserMapper userMapper;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.create(user));
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Integer id) {
        return userMapper.toUserDto(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public UserDto patch(@PathVariable Integer id, @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.put(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
