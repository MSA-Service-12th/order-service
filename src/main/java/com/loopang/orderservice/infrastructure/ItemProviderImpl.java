package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.ItemProvider;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.vo.OrderItemInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemProviderImpl implements ItemProvider {

	// TODO: FeignClient 연동 후 ItemClient 추가

	@Override
	public ItemData getItem(UUID itemId) {
		throw new UnsupportedOperationException("ItemClient Feign 연동이 필요합니다");
	}
}
