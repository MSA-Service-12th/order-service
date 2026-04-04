package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.service.dto.HubData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", fallbackFactory = HubFeignClientFallbackFactory.class)
public interface HubFeignClient {

	@GetMapping("/api/hubs/{hubId}")
	HubData getHubData(@PathVariable("hubId") UUID hubId);
}
