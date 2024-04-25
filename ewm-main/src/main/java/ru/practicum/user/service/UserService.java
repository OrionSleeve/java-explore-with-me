package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers(Integer[] ids, Integer from, Integer size);

    void removeUser(Integer userId);
}
