package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.UserProvider;
import com.loopang.orderservice.domain.service.dto.UserData;
import com.loopang.orderservice.infrastructure.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

	private final UserFeignClient userFeignClient;

	@Override
	public UserData getUser(UUID userId) {
		return userFeignClient.getUserData(userId);
	}
}
