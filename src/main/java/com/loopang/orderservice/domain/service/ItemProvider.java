package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.ItemData;

import java.util.UUID;

public interface ItemProvider {

	// 주문 요청 시 전달한 상품ID에 해당하는 상품정보를 가져오기 위해 호출
	ItemData getItem(UUID itemId);
}
