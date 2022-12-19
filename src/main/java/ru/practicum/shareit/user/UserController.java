package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    public final UserService userService;
    @Autowired
    private final UserMapper userMapper;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.create(userMapper.toUser(userDto)));
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Integer id) {
        return userMapper.toUserDto(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public UserDto patch(@PathVariable Integer id, @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.put(id, userMapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }
}
