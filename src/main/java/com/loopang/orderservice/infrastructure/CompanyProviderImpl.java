package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.CompanyProvider;
import com.loopang.orderservice.domain.service.OrderValidator;
import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;
import com.loopang.orderservice.infrastructure.client.CompanyFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyProviderImpl implements CompanyProvider {

	private final CompanyFeignClient companyFeignClient;
	private final OrderValidator orderValidator;

	@Override
	public SupplierData getSupplier(UUID supplierId) {
		return companyFeignClient.getSupplier(supplierId);
	}

	@Override
	public ReceiverData getReceiver(UUID receiverId) {
		return companyFeignClient.getReceiver(receiverId);
	}
}
