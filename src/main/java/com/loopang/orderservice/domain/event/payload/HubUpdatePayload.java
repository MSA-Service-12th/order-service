package com.loopang.orderservice.domain.event.payload;

import java.util.UUID;

// 허브 -> 주문 방향으로 오는 이벤트 데이터
public record HubUpdatePayload(
		UUID orderId,
		UUID hubId,
		UUID hubInventoryId,
		int quantity,
		int balance // 재고 차감 연산 후 남은 수량 (음수면 재고 부족)
) { }
