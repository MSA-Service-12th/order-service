package com.loopang.orderservice.infrastructure.client;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

	@Override
	public UserFeignClient create(Throwable cause) {
		return id -> {
			log.error("[User service Fallback] ID: {} 조회 중 장애 발생, 사유: {}",
					id, cause.getMessage(), cause); // 발생위치 -> 파생위치를 알려줌 stackTrace
			throw new InternalServerErrorException("User Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.");
		};
	}
}
