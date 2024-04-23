package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity savedUser = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers(List<Integer> ids, Pageable pageForUsers) {
        List<UserEntity> byIds;

        if (ids != null && !ids.isEmpty()) {
            byIds = userRepository.findByIdIn(ids, pageForUsers);
        } else {
            byIds = userRepository.findAll(pageForUsers).toList();
        }

        return byIds.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void removeUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
    }
}
