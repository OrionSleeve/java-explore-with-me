package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

public interface UserSubService {
    UserDto addSubscription(Integer userId, Integer subscribeOnId);

    UserDto cancelSubscription(Integer userId, Integer subscribedOnId);

    UserDto updateSubscriptionAvailable(Integer userId, Boolean isAvailable);

    UserDto getUserById(Integer userId);
}
