package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.UserData;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.Optional;
import java.util.UUID;

public interface UserProvider {

    UserData getUser(UUID userId);

    default UUID getHubIdIfHubManager(UUID userId, UserType userType) {
        if (userType != UserType.HUB) {
            return null;
        }
        return Optional.ofNullable(getUser(userId))
                .map(UserData::hubId)
                .orElse(null);
    }
}
