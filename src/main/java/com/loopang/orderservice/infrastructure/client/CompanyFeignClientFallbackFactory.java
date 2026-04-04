package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompanyFeignClientFallbackFactory implements FallbackFactory<CompanyFeignClient> {

	@Override
	public CompanyFeignClient create(Throwable cause) {
		return id -> {
			log.error("[Company service Fallback] ID: {} 조회 중 장애 발생, 사유: {}",
					id, cause.getMessage(), cause);
			throw new OrderException(
					HttpStatus.SERVICE_UNAVAILABLE,
					"Company Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.",
					"company-service"
			);
		};
	}
}
