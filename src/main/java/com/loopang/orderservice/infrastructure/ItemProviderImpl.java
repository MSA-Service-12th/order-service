package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.ItemProvider;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.vo.OrderItemInfo;
import com.loopang.orderservice.infrastructure.client.ItemFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemProviderImpl implements ItemProvider {

	private final ItemFeignClient itemFeignClient;

	@Override
	public ItemData getItem(UUID itemId) {
		return itemFeignClient.getItemData(itemId);
	}
}
