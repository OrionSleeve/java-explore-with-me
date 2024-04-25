package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.mapper.UserShortMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Page;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserShortMapper userShortMapper


    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity.setSubscriptionAvailable(Objects.requireNonNullElse(userEntity.getSubscriptionAvailable(),
                true));
        UserEntity savedUser = userRepository.save(userEntity);
        UserDto createdUserDto = userMapper.toDto(savedUser);
        createdUserDto.setSubscribedOn(new ArrayList<>());
        return createdUserDto;
    }

    @Override
    public List<UserDto> getAllUsers(Integer[] ids, Integer from, Integer size) {
        Pageable pageable = Page.getPageForUsers(from, size);
        List<UserEntity> userEntities;
        if (ids == null || ids.length == 0) {
            userEntities = userRepository.findAll(pageable).toList();
        } else {
            userEntities = userRepository.findByIdIn(List.of(ids), pageable);
        }

        Set<Integer> subscribedOn = new HashSet<>();
        userEntities.forEach(userEntity -> subscribedOn.addAll(userEntity.getSubscribedOn()));

        List<UserEntity> subscribedOnUserEntities = userRepository.findByIdIn(subscribedOn);
        List<UserDto> result = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            List<UserShortDto> subscriptions = subscribedOnUserEntities.stream()
                    .filter(userEntity1 -> userEntity.getSubscribedOn().contains(userEntity1.getId()))
                    .map(userShortMapper::toDto)
                    .sorted(Comparator.comparing(UserShortDto::getId)).collect(Collectors.toList());

            UserDto userDto = userMapper.toDto(userEntity);
            userDto.setSubscribedOn(subscriptions);
            result.add(userDto);
        }
        return result;
    }

    @Override
    public void removeUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
    }
}
