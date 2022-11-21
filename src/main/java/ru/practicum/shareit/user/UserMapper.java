package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@RequiredArgsConstructor
public class UserMapper {
    @Autowired
    private final ModelMapper modelMapper;
    public UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User toUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
