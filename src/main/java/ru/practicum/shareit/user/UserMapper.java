package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
