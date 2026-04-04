package com.loopang.orderservice.infrastructure.client;

import com.loopang.orderservice.domain.service.dto.ItemData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "item-service", fallbackFactory = ItemFeignClientFallbackFactory.class)
public interface ItemFeignClient {

	@GetMapping("/api/items/{itemId}")
	ItemData getItemData(@PathVariable("itemId") UUID itemId);
}
