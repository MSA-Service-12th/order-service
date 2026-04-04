package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.service.dto.UserData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", fallbackFactory = UserFeignClientFallbackFactory.class)
public interface UserFeignClient {

	@GetMapping("/api/users/{userId}")
	UserData getUserData(@PathVariable("userId") UUID userId);
}
