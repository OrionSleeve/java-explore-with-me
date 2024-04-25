package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.SubscriptionException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.mapper.UserShortMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSubServiceImpl implements UserSubService {
    private final UserRepository userRepository;
    private final UserShortMapper shortUserMapper;
    private final UserMapper userMapper;

    @Override
    public UserDto addSubscription(Integer userId, Integer subscribeOnId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        UserEntity subscribeOnUser = userRepository.findById(subscribeOnId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", subscribeOnId)));

        if (userId.equals(subscribeOnId)) {
            throw new SubscriptionException("The user can not subscribe to himself");
        }

        if (!subscribeOnUser.getSubscriptionAvailable()) {
            throw new SubscriptionException(String.format("The user with id=%d has banned " +
                    "the subscription on himself", subscribeOnId));
        }

        if (user.getSubscribedOn().contains(subscribeOnId)) {
            throw new SubscriptionException(String.format("The user with id=%d is already subscribed" +
                    " to user with id=%d", userId, subscribeOnId));
        }
        user.getSubscribedOn().add(subscribeOnId);
        user = userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);
        userDto.setSubscribedOn(getSubscribedOn(user));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto cancelSubscription(Integer userId, Integer subscribedOnId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        userRepository.findById(subscribedOnId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", subscribedOnId)));
        if (!user.getSubscribedOn().contains(subscribedOnId)) {
            throw new SubscriptionException(String.format("The user with id=%d is not subscribed" +
                    " to user with id=%d", userId, subscribedOnId));
        }
        user.getSubscribedOn().remove(subscribedOnId);
        user = userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);
        userDto.setSubscribedOn(getSubscribedOn(user));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateSubscriptionAvailable(Integer userId, Boolean isAvailable) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        if (user.getSubscriptionAvailable().equals(isAvailable)) {
            throw new SubscriptionException(String.format("Subscription availability for user " +
                    "with id=%d is already %s", userId, isAvailable));
        }
        user.setSubscriptionAvailable(isAvailable);
        user = userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);
        userDto.setSubscribedOn(getSubscribedOn(user));
        return userDto;

    }

    @Override
    @Transactional
    public UserDto getUserById(Integer userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        UserDto userDto = userMapper.toDto(user);
        userDto.setSubscribedOn(getSubscribedOn(user));
        return userDto;
    }

    private List<UserShortDto> getSubscribedOn(UserEntity user) {
        List<UserEntity> subscribedOn = userRepository.findByIdIn(user.getSubscribedOn());
        return subscribedOn.stream().map(shortUserMapper::toDto)
                .sorted(Comparator.comparing(UserShortDto::getId)).collect(Collectors.toList());

    }
}
