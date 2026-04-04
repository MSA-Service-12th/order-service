package com.loopang.orderservice.infrastructure.client;

import org.springframework.cloud.openfeign.FallbackFactory;

public class HubFeignClientFallbackFactory implements FallbackFactory<HubFeignClient> {

	@Override
	public HubFeignClient create(Throwable cause) {
		return null;
	}
}
