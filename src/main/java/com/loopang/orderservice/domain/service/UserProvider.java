package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.UserData;

import java.util.UUID;

public interface UserProvider {

	UserData getUser(UUID userId);
}
