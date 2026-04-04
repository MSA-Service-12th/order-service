package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.service.dto.CompanyData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", fallbackFactory = CompanyFeignClientFallbackFactory.class)
public interface CompanyFeignClient {

	@GetMapping("/api/companies/{id}")
	CompanyData getCompanyData(@PathVariable("id") UUID id);
}
