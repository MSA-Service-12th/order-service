package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.UserData;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.Optional;
import java.util.UUID;

public interface UserProvider {

    UserData getUser(UUID userId);

    default UUID getHubIdIfHubManager(UUID userId, UserType userType) {
        UserData user = getUser(userId);
        if (userType != UserType.HUB || user == null) {
            return null;
        }
        return Optional.ofNullable(getUser(userId))
                .map(UserData::hubId)
                .orElse(null);
    }
}
