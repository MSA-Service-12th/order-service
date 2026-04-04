package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.service.dto.CourierData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "delivery-service", fallbackFactory = DeliveryFeignClientFallbackFactory.class)
public interface DeliveryFeignClient {

	@GetMapping("/api/deliveries/{deliveryId}")
	CourierData getCourierByOrder(@PathVariable("deliveryId") UUID deliveryId);
}
