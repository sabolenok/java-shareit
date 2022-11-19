package ru.practicum.shareit.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    @Autowired
    private static ModelMapper modelMapper;
    public static UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
