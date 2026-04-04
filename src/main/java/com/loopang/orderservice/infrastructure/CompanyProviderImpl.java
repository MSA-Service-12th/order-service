package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.CompanyProvider;
import com.loopang.orderservice.domain.service.OrderValidator;
import com.loopang.orderservice.domain.service.dto.CompanyData;
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
	public CompanyData getCompany(UUID companyId) {
		CompanyData companyData = companyFeignClient.getCompanyData(companyId);

		orderValidator.validateCompany(companyData);

		return companyData;
	}
}

