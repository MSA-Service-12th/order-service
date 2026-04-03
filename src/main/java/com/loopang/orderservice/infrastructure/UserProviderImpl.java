package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.UserProvider;
import com.loopang.orderservice.domain.service.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

	// TODO: FeignClient 연동 후 UserClient 주입

	@Override
	public UserData getUserData(UUID userId) {
		throw new UnsupportedOperationException("UserClient Feign 연동 필수");
	}
}
