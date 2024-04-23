package ru.practicum.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers(List<Integer> ids, Pageable pageForUsers);

    void removeUser(Integer userId);
}
