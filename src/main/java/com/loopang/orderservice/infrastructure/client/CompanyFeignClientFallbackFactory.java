package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CompanyFeignClientFallbackFactory implements FallbackFactory<CompanyFeignClient> {

	@Override
	public CompanyFeignClient create(Throwable cause) {
		return new CompanyFeignClient() {
			@Override
			public SupplierData getSupplier(UUID supplierId) {
				log.error("[Company service Fallback] Supplier ID: {} 조회 중 장애 발생, 사유: {}",
						supplierId, cause.getMessage(), cause); // 발생위치 -> 파생위치를 알려줌 stackTrace
				throw new OrderException(
						HttpStatus.SERVICE_UNAVAILABLE,
						"Company Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.",
						"company-service"
				);
			}

			@Override
			public ReceiverData getReceiver(UUID receiverId) {
				log.error("[Company service Fallback] Receiver ID: {} 조회 중 장애 발생, 사유: {}",
						receiverId, cause.getMessage(), cause); // 발생위치 -> 파생위치를 알려줌 stackTrace
				throw new OrderException(
						HttpStatus.SERVICE_UNAVAILABLE,
						"Company Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.",
						"company-service"
				);
			}
		};
	}
}
