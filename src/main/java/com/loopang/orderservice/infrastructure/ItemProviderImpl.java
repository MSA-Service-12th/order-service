package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.ItemProvider;
import com.loopang.orderservice.domain.service.OrderValidator;
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
	private final OrderValidator orderValidator;

	@Override
	public ItemData getItem(UUID itemId) {
		ItemData itemData = itemFeignClient.getItemData(itemId);

		orderValidator.validateItem(itemData);

		return itemData;
	}
}
