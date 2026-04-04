package com.loopang.orderservice.infrastructure.client;

import org.springframework.cloud.openfeign.FallbackFactory;

public class CompanyFeignClientFallbackFactory implements FallbackFactory<CompanyFeignClient> {

	@Override
	public CompanyFeignClient create(Throwable cause) {
		return null;
	}
}
